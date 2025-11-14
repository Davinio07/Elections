package nl.hva.elections.mover;

import nl.hva.elections.persistence.model.Candidate;
import nl.hva.elections.xml.model.Party;
import nl.hva.elections.repositories.CandidateRepository;
import nl.hva.elections.repositories.PartyRepository;
import nl.hva.elections.xml.service.NationalResultService;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.KiesKring;
import nl.hva.elections.xml.service.DutchElectionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DataInitializer that:
 * - Builds correct national totals by choosing the best source (nationalResults when clean, otherwise summing municipality results)
 * - Aggregates duplicate party entries (summing votes) to avoid inflated totals
 * - Calculates seats & percentages and upserts Party entities
 * - Persists Candidate entities while avoiding duplicates
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final DutchElectionService xmlService;
    private final NationalResultService nationalResultService;
    private final PartyRepository partyRepository;
    private final CandidateRepository candidateRepository;

    private static final List<String> ELECTION_LIST = List.of("TK2023", "TK2021");
    private static final int TOTAL_SEATS = 150;

    public DataInitializer(DutchElectionService xmlService,
                           PartyRepository partyRepository,
                           CandidateRepository candidateRepository,
                           NationalResultService nationalResultService) {
        this.xmlService = xmlService;
        this.partyRepository = partyRepository;
        this.candidateRepository = candidateRepository;
        this.nationalResultService = nationalResultService;
    }

    @Override
    public void run(String... args) throws Exception {
        // If DB already seeded, skip
        if (partyRepository.count() > 0 || candidateRepository.count() > 0) {
            System.out.println("Database already contains data. Skipping data load.");
            return;
        }

        System.out.println("Database is empty. Loading data from XML for " + ELECTION_LIST.size() + " elections...");

        for (String electionId : ELECTION_LIST) {
            System.out.println("--- Loading data for: " + electionId + " ---");

            Election electionData = xmlService.getElectionData(electionId);
            if (electionData == null) {
                System.err.println("CRITICAL: No cached data found for " + electionId + ". Skipping.");
                continue;
            }

            // ---------------------------
            // 1) Build reliable national totals
            // ---------------------------
            // Sources:
            // - electionData.getNationalResults() may contain the official national totals (preferred if clean)
            // - electionData.getMunicipalityResults() can be summed to derive national totals (fallback)
            List<Party> nationalResults = electionData.getNationalResults();
            List<KiesKring> municipalityResults = electionData.getMunicipalityResults();

            Map<String, Integer> aggregatedVotes = new HashMap<>();

            boolean useNationalResultsDirectly = false;
            if (nationalResults != null && !nationalResults.isEmpty()) {
                long distinctNames = nationalResults.stream()
                        .map(Party::getName)
                        .distinct()
                        .count();
                // If nationalResults contains one entry per party (no duplicates), prefer it.
                if (distinctNames == nationalResults.size()) {
                    useNationalResultsDirectly = true;
                }
            }

            if (useNationalResultsDirectly) {
                // Aggregate (defensive: still sum duplicates if they exist)
                aggregatedVotes = nationalResults.stream()
                        .collect(Collectors.toMap(
                                Party::getName,
                                Party::getVotes,
                                Integer::sum
                        ));
            } else {
                // Prefer summing municipality results if available (should produce correct national totals)
                if (municipalityResults != null && !municipalityResults.isEmpty()) {
                    aggregatedVotes = municipalityResults.stream()
                            .collect(Collectors.toMap(
                                    KiesKring::getPartyName,
                                    KiesKring::getValidVotes,
                                    Integer::sum
                            ));
                } else {
                    // Fallback: use nationalResults aggregated by name (defensive)
                    aggregatedVotes = (nationalResults == null) ? new HashMap<>() :
                            nationalResults.stream()
                                    .collect(Collectors.toMap(
                                            Party::getName,
                                            Party::getVotes,
                                            Integer::sum
                                    ));
                }
            }

            if (aggregatedVotes.isEmpty()) {
                System.err.println("No vote data available for " + electionId + ". Skipping.");
                continue;
            }

            // Build list of Party objects (xml.model.Party) for seat calculation
            List<Party> aggregatedPartyList = aggregatedVotes.entrySet().stream()
                    .map(e -> {
                        Party p = new Party();
                        p.setName(e.getKey());
                        p.setVotes(e.getValue());
                        return p;
                    })
                    .collect(Collectors.toList());

            // ---------------------------
            // 2) Calculate seats & percentages
            // ---------------------------
            Map<String, Integer> calculatedSeatsMap = nationalResultService.calculateSeats(aggregatedPartyList, TOTAL_SEATS);

            double totalVotes = aggregatedPartyList.stream().mapToDouble(Party::getVotes).sum();

            // ---------------------------
            // 3) Persist parties (upsert: insert if missing, update if exists)
            //    Only one Party entity per (name, electionId) will be stored.
            // ---------------------------
            AtomicInteger partiesSaved = new AtomicInteger(0);
            for (Map.Entry<String, Integer> entry : aggregatedVotes.entrySet()) {
                String partyName = entry.getKey();
                int votes = entry.getValue();
                int seats = calculatedSeatsMap.getOrDefault(partyName, 0);
                double percentage = (totalVotes > 0) ? ((double) votes / totalVotes) * 100.0 : 0.0;

                // Try to find existing Party row for this election+name
                partyRepository.findByNameAndElectionId(partyName, electionId).ifPresentOrElse(existing -> {
                    // Update values if needed
                    boolean changed = false;
                    if (existing.getVotes() != votes) { existing.setVotes(votes); changed = true; }
                    if (existing.getSeats() != seats) { existing.setSeats(seats); changed = true; }
                    if (Double.compare(existing.getPercentage(), percentage) != 0) { existing.setPercentage(percentage); changed = true; }

                    if (changed) {
                        partyRepository.save(existing);
                    }
                    // count but do not increment 'saved' for updates
                }, () -> {
                    // Create new JPA Party entity
                    Party jpaParty = new Party();
                    jpaParty.setElectionId(electionId);
                    jpaParty.setName(partyName);
                    jpaParty.setVotes(votes);
                    jpaParty.setSeats(seats);
                    jpaParty.setPercentage(percentage);
                    partyRepository.save(jpaParty);
                    partiesSaved.incrementAndGet();
                });
            }

            System.out.println("Persisted parties for " + electionId + ". New saved: " + partiesSaved.get()
                    + " Total distinct parties: " + aggregatedVotes.size());

            // ---------------------------
            // 4) Persist candidates (only if DB empty for candidates)
            //    Candidate -> link to party via findByNameAndElectionId (which now should be unique)
            // ---------------------------
            if (candidateRepository.count() == 0) {
                System.out.println("Loading candidate data from XML for " + electionId + "...");
                final AtomicInteger candidatesSaved = new AtomicInteger(0);

                for (nl.hva.elections.xml.model.Candidate xmlCandidate : electionData.getCandidates()) {
                    String partyName = xmlCandidate.getPartyName();
                    if (partyName == null || partyName.isBlank()) continue;
                    String cleanedPartyName = partyName.trim();
                    String candidateFullName = (xmlCandidate.getFirstName() + " " + xmlCandidate.getLastName()).trim();

                    // Find the party (should be unique now)
                    partyRepository.findByNameAndElectionId(cleanedPartyName, electionId).ifPresentOrElse(jpaParty -> {
                        // Skip duplicate candidate entries
                        if (candidateRepository.existsByNameAndPartyId(candidateFullName, jpaParty.getId())) {
                            return;
                        }
                        Candidate newCandidate = new Candidate();
                        newCandidate.setName(candidateFullName);
                        newCandidate.setResidence(xmlCandidate.getLocality());
                        newCandidate.setGender(xmlCandidate.getGender());
                        newCandidate.setParty(jpaParty);
                        candidateRepository.save(newCandidate);
                        candidatesSaved.incrementAndGet();
                    }, () -> {
                        System.err.println("No Party found in DB with name: [" + cleanedPartyName + "] for candidate " + candidateFullName);
                    });
                }

                System.out.println("Finished loading candidate data. Total candidates saved: " + candidatesSaved.get());
            }
        } // end for each election
    }
}
