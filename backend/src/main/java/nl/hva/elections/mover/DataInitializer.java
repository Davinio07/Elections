package nl.hva.elections.mover;

import nl.hva.elections.persistence.model.Candidate;
import nl.hva.elections.persistence.model.Gemeente;
import nl.hva.elections.persistence.model.Kieskring;
import nl.hva.elections.repositories.*;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.MunicipalityResult;
import nl.hva.elections.xml.model.Party;
import nl.hva.elections.xml.model.Region;
import nl.hva.elections.xml.service.DutchElectionService;
import nl.hva.elections.xml.service.NationalResultService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Order(2)
public class DataInitializer implements CommandLineRunner {

    private final DutchElectionService xmlService;
    private final NationalResultService nationalResultService;
    private final PartyRepository partyRepository;
    private final CandidateRepository candidateRepository;
    private final KieskringRepository kieskringRepository;
    private final ProvinceRepository provinceRepository;
    private final GemeenteRepository gemeenteRepository;

    private static final List<String> ELECTION_LIST = List.of("TK2023", "TK2021");
    private static final int TOTAL_SEATS = 150;

    public DataInitializer(DutchElectionService xmlService,
                           PartyRepository partyRepository,
                           CandidateRepository candidateRepository,
                           NationalResultService nationalResultService,
                           GemeenteRepository gemeenteRepository,
                           KieskringRepository kieskringRepository,
                           ProvinceRepository provinceRepository) {
        this.xmlService = xmlService;
        this.partyRepository = partyRepository;
        this.candidateRepository = candidateRepository;
        this.nationalResultService = nationalResultService;
        this.kieskringRepository = kieskringRepository;
        this.gemeenteRepository = gemeenteRepository;
        this.provinceRepository = provinceRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (provinceRepository.count() == 0) {
            System.err.println("CRITICAL WARNING: Province table is empty! Run DataSeeder first.");
            return;
        }

        System.out.println("--- Starting Data Sync (Geography + Results) ---");

        for (String electionId : ELECTION_LIST) {
            Election electionData = xmlService.getElectionData(electionId);
            if (electionData == null) {
                System.err.println("No data found for " + electionId);
                continue;
            }

            System.out.println("Processing election: " + electionId);

            // ------------------------------------------------------------------
            // 1. SYNC KIESKRINGEN (Constituencies)
            // ------------------------------------------------------------------
            Map<String, String> kieskringXmlIdToNameMap = new HashMap<>();
            List<Region> regions = electionData.getRegions();

            List<Region> kieskringRegions = regions.stream()
                    .filter(r -> "KIESKRING".equalsIgnoreCase(r.getCategory()))
                    .toList();

            for (Region r : kieskringRegions) {
                kieskringXmlIdToNameMap.put(r.getId(), r.getName());

                Kieskring kieskring = kieskringRepository.findByName(r.getName())
                        .orElse(new Kieskring(r.getName()));

                int provinceId = getProvinceIdByName(r.getName());
                if (provinceId > 0) {
                    provinceRepository.findById(provinceId).ifPresent(kieskring::setProvince);
                }
                kieskringRepository.save(kieskring);
            }

            // ------------------------------------------------------------------
            // 2. SYNC GEMEENTEN (Municipalities)
            // ------------------------------------------------------------------
            if (gemeenteRepository.count() == 0) {
                System.out.println("Syncing Gemeenten...");
                List<Region> gemeenteRegions = regions.stream()
                        .filter(r -> "GEMEENTE".equalsIgnoreCase(r.getCategory()))
                        .toList();

                for (Region r : gemeenteRegions) {
                    if (gemeenteRepository.findByName(r.getName()).isPresent()) continue;

                    Gemeente gemeente = new Gemeente(r.getName());
                    String parentName = kieskringXmlIdToNameMap.get(r.getSuperiorRegionNumber());

                    if (parentName != null) {
                        kieskringRepository.findByName(parentName).ifPresent(k -> {
                            gemeente.setKieskring(k);
                            gemeente.setProvince(k.getProvince());
                        });
                    }
                    gemeenteRepository.save(gemeente);
                }
            }

            // ------------------------------------------------------------------
            // 3. SYNC PARTIES & RESULTS
            // ------------------------------------------------------------------
            // Only run this if parties don't exist yet for this election
            if (partyRepository.findByElectionId(electionId).isEmpty()) {
                System.out.println("Syncing Parties & Results for " + electionId + "...");

                List<Party> nationalResults = electionData.getNationalResults();
                List<MunicipalityResult> municipalityResults = electionData.getMunicipalityResults();
                Map<String, Integer> aggregatedVotes = new HashMap<>();

                boolean useNationalResultsDirectly = false;
                if (nationalResults != null && !nationalResults.isEmpty()) {
                    long distinctNames = nationalResults.stream().map(Party::getName).distinct().count();
                    if (distinctNames == nationalResults.size()) useNationalResultsDirectly = true;
                }

                if (useNationalResultsDirectly) {
                    aggregatedVotes = nationalResults.stream()
                            .collect(Collectors.toMap(Party::getName, Party::getVotes, Integer::sum));
                } else if (municipalityResults != null && !municipalityResults.isEmpty()) {
                    aggregatedVotes = municipalityResults.stream()
                            .collect(Collectors.toMap(MunicipalityResult::getPartyName, MunicipalityResult::getValidVotes, Integer::sum));
                } else {
                    aggregatedVotes = (nationalResults == null) ? new HashMap<>() :
                            nationalResults.stream().collect(Collectors.toMap(Party::getName, Party::getVotes, Integer::sum));
                }

                if (aggregatedVotes.isEmpty()) {
                    System.out.println("No votes found for " + electionId);
                    continue;
                }

                // Calculate Seats
                List<Party> partyListForCalc = aggregatedVotes.entrySet().stream()
                        .map(e -> { Party p = new Party(); p.setName(e.getKey()); p.setVotes(e.getValue()); return p; })
                        .collect(Collectors.toList());

                Map<String, Integer> seatsMap = nationalResultService.calculateSeats(partyListForCalc, TOTAL_SEATS);
                double totalVotes = partyListForCalc.stream().mapToDouble(Party::getVotes).sum();

                // Persist Parties
                for (Map.Entry<String, Integer> entry : aggregatedVotes.entrySet()) {
                    String partyName = entry.getKey();
                    int votes = entry.getValue();
                    int seats = seatsMap.getOrDefault(partyName, 0);
                    double percentage = (totalVotes > 0) ? ((double) votes / totalVotes) * 100.0 : 0.0;

                    Party jpaParty = new Party();
                    jpaParty.setElectionId(electionId);
                    jpaParty.setName(partyName);
                    jpaParty.setVotes(votes);
                    jpaParty.setSeats(seats);
                    jpaParty.setPercentage(percentage);
                    partyRepository.save(jpaParty);
                }
            } else {
                System.out.println("Parties already exist for " + electionId + ". Skipping.");
            }

            // ------------------------------------------------------------------
            // 4. SYNC CANDIDATES
            // ------------------------------------------------------------------
            if (candidateRepository.count() == 0) {
                System.out.println("Syncing Candidates for " + electionId + "...");
                AtomicInteger candidatesSaved = new AtomicInteger(0);

                for (nl.hva.elections.xml.model.Candidate xmlCandidate : electionData.getCandidates()) {
                    String partyName = xmlCandidate.getPartyName();
                    if (partyName == null || partyName.isBlank()) continue;

                    String cleanName = partyName.trim();
                    String fullName = (xmlCandidate.getFirstName() + " " + xmlCandidate.getLastName()).trim();

                    partyRepository.findByNameAndElectionId(cleanName, electionId).ifPresent(party -> {
                        if (!candidateRepository.existsByNameAndPartyId(fullName, party.getId())) {
                            Candidate c = new Candidate();
                            c.setName(fullName);
                            c.setResidence(xmlCandidate.getLocality());
                            c.setGender(xmlCandidate.getGender());
                            c.setParty(party);
                            candidateRepository.save(c);
                            candidatesSaved.incrementAndGet();
                        }
                    });
                }
                System.out.println("Saved " + candidatesSaved.get() + " candidates.");
            }
        }

        System.out.println("Data sync complete.");
    }

    // Helper for Province IDs
    private int getProvinceIdByName(String name) {
        if (name == null) return -1;
        return switch (name.trim()) {
            case "Groningen" -> 1;
            case "Leeuwarden" -> 2;
            case "Assen" -> 3;
            case "Zwolle" -> 4;
            case "Lelystad" -> 5;
            case "Nijmegen", "Arnhem" -> 6;
            case "Utrecht" -> 7;
            case "Amsterdam", "Haarlem", "Den Helder" -> 8;
            case "'s-Gravenhage", "Rotterdam", "Dordrecht", "Leiden" -> 9;
            case "Middelburg" -> 10;
            case "Tilburg", "'s-Hertogenbosch" -> 11;
            case "Maastricht", "Bonaire" -> 12;
            default -> -1;
        };
    }
}