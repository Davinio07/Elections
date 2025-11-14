package nl.hva.elections.mover;

import nl.hva.elections.persistence.model.Candidate;
import nl.hva.elections.xml.model.Party;
import nl.hva.elections.repositories.CandidateRepository;
import nl.hva.elections.repositories.PartyRepository;
import nl.hva.elections.xml.service.NationalResultService;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.service.DutchElectionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Initializes the database with election data from XML files upon application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final DutchElectionService xmlService;
    private final NationalResultService nationalResultService;
    private final PartyRepository partyRepository;
    private final CandidateRepository candidateRepository;

    private static final List<String> electionList = List.of("TK2023", "TK2021");
    private static final int totalSeats = 150;

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
        // Skip initialization if DB already has data
        if (partyRepository.count() > 0 || candidateRepository.count() > 0) {
            System.out.println("Database already contains data. Skipping data load.");
            return;
        }

        System.out.println("Database is empty. Loading data from XML for " + electionList.size() + " elections...");

        for (String electionId : electionList) {
            System.out.println("--- Loading data for: " + electionId + " ---");

            Election electionData = xmlService.getElectionData(electionId);
            if (electionData == null) {
                System.err.println("CRITICAL: No cached data found for " + electionId + ". Skipping.");
                continue;
            }

            // --- 1. Process and Save Parties ---
            List<Party> rawResults = electionData.getNationalResults();
            if (rawResults == null || rawResults.isEmpty()) {
                System.err.println("No national results found for " + electionId + ". Skipping party load.");
                continue;
            }

            // Aggregate votes per party to avoid duplicates
            Map<String, Integer> aggregatedVotes = rawResults.stream()
                    .collect(Collectors.toMap(
                            Party::getName,
                            Party::getVotes,
                            Integer::sum // merge duplicate votes
                    ));

            // Rebuild Party objects from aggregated votes
            List<Party> uniqueParties = aggregatedVotes.entrySet().stream()
                    .map(e -> {
                        Party p = new Party();
                        p.setName(e.getKey());
                        p.setVotes(e.getValue());
                        return p;
                    }).collect(Collectors.toList());

            // Calculate seats using NationalResultService
            Map<String, Integer> calculatedSeatsMap = nationalResultService.calculateSeats(uniqueParties, totalSeats);

            // Total votes for percentage calculation
            double totalVotes = uniqueParties.stream()
                    .mapToDouble(Party::getVotes)
                    .sum();

            // Persist Parties
            for (Party party : uniqueParties) {
                String partyName = party.getName();
                int votes = party.getVotes();
                int calculatedSeats = calculatedSeatsMap.getOrDefault(partyName, 0);
                double calculatedPercentage = (totalVotes > 0) ? ((double) votes / totalVotes) * 100.0 : 0.0;

                // Skip if already in DB
                if (partyRepository.findByNameAndElectionId(partyName, electionId).isPresent()) {
                    System.out.println("Skipping duplicate party: " + partyName);
                    continue;
                }

                Party jpaParty = new Party();
                jpaParty.setElectionId(electionId);
                jpaParty.setName(partyName);
                jpaParty.setVotes(votes);
                jpaParty.setSeats(calculatedSeats);
                jpaParty.setPercentage(calculatedPercentage);

                partyRepository.save(jpaParty);
            }

            System.out.println("Saved " + uniqueParties.size() + " parties for " + electionId + " with calculated seats/percentages.");

            // --- 2. Save Candidates ---
            if (candidateRepository.count() == 0) {
                System.out.println("Loading candidate data from XML...");
                final AtomicInteger candidatesSaved = new AtomicInteger(0);

                for (nl.hva.elections.xml.model.Candidate xmlCandidate : electionData.getCandidates()) {
                    String partyName = xmlCandidate.getPartyName();
                    if (partyName == null || partyName.isBlank()) continue;

                    String cleanedPartyName = partyName.trim();
                    String candidateFullName = (xmlCandidate.getFirstName() + " " + xmlCandidate.getLastName()).trim();

                    partyRepository.findByNameAndElectionId(cleanedPartyName, electionId).ifPresentOrElse(jpaParty -> {
                        // Skip duplicates
                        if (candidateRepository.existsByNameAndPartyId(candidateFullName, jpaParty.getId())) {
                            System.out.println("Skipping duplicate candidate: " + candidateFullName);
                            return;
                        }

                        Candidate newCandidate = new Candidate();
                        newCandidate.setName(candidateFullName);
                        newCandidate.setResidence(xmlCandidate.getLocality());
                        newCandidate.setGender(xmlCandidate.getGender());
                        newCandidate.setParty(jpaParty);

                        candidateRepository.save(newCandidate);
                        candidatesSaved.incrementAndGet();
                    }, () -> System.err.println("No Party found in DB with name: [" + cleanedPartyName + "]"));
                }

                System.out.println("Finished loading candidate data. Total candidates saved: " + candidatesSaved.get());
            }
        }
    }
}
