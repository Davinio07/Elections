package nl.hva.elections.xml.api;

import nl.hva.elections.xml.model.*;
import nl.hva.elections.xml.service.DutchElectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.hva.elections.persistence.model.Candidate;
import nl.hva.elections.repositories.CandidateRepository;
import nl.hva.elections.repositories.PartyRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/elections")
public class ElectionController {

    private static final Logger logger = LoggerFactory.getLogger(ElectionController.class);
    private final DutchElectionService electionService;
    private final CandidateRepository candidateRepository;
    private final PartyRepository partyRepository;

    // Removed ProvinceRepository and KieskringRepository from constructor
    public ElectionController(DutchElectionService electionService, CandidateRepository candidateRepository, PartyRepository partyRepository) {
        this.electionService = electionService;
        this.candidateRepository = candidateRepository;
        this.partyRepository = partyRepository;
    }

    /**
     * Haalt de "standaard" (TK2023) verkiezingsdata op.
     */
    @GetMapping("/results")
    public ResponseEntity<Election> getElectionResults() {
        logger.info("Request received for default election data.");
        Election election = electionService.loadAllElectionData();
        return ResponseEntity.ok(election);
    }

    @PostMapping("{electionId}")
    public Election readResults(@PathVariable String electionId, @RequestParam(required = false) String folderName) {
        logger.info("Reading results for electionId: {}", electionId);
        return electionService.readResults(electionId, folderName);
    }

    @GetMapping("{electionId}/regions")
    public ResponseEntity<List<Region>> getRegions(@PathVariable String electionId) {
        try {
            logger.info("Fetching regions for election: {}", electionId);
            List<Region> regions = electionService.getRegions(electionId);
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            logger.error("Error fetching regions for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // MOVED: getKieskringen -> KieskringController

    @GetMapping("{electionId}/regions/gemeenten")
    public ResponseEntity<List<Region>> getGemeenten(@PathVariable String electionId) {
        try {
            logger.info("Fetching gemeenten for election: {}", electionId);
            List<Region> gemeenten = electionService.getGemeenten(electionId);
            return ResponseEntity.ok(gemeenten);
        } catch (Exception e) {
            logger.error("Error fetching gemeenten for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // --- DB Endpoints ---

    @GetMapping("/candidates/db")
    public ResponseEntity<List<Candidate>> getAllCandidatesFromDb(
            @RequestParam(required = false) Long partyId,
            @RequestParam(required =false) String gender) {
        try {
            List<Candidate> candidates;
            if (partyId != null && gender != null) {
                candidates = candidateRepository.findByPartyIdAndGender(partyId, gender);
            } else if (partyId != null) {
                candidates = candidateRepository.findByPartyId(partyId);
            } else if (gender != null) {
                candidates = candidateRepository.findByGender(gender);
            } else {
                candidates = candidateRepository.findAll();
            }
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/test-election")
    public ResponseEntity<List<Party>> testForRepo(@RequestParam("id") String electionId) {
        List<Party> parties = partyRepository.findByElectionId(electionId);
        return ResponseEntity.ok(parties);
    }

    // --- Einde DB Endpoints ---

    @GetMapping("{electionId}/parties")
    public ResponseEntity<List<PoliticalParty>> getPoliticalParties(@PathVariable String electionId) {
        try {
            logger.info("Fetching political parties for election: {}", electionId);
            List<PoliticalParty> parties = electionService.getPoliticalParties(electionId);
            return ResponseEntity.ok(parties);
        } catch (Exception e) {
            logger.error("Error fetching parties for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("{electionId}/parties/search")
    public ResponseEntity<PoliticalParty> findPartyByName(
            @PathVariable String electionId,
            @RequestParam String partyName) {
        try {
            logger.info("Searching for party '{}' in election '{}'", partyName, electionId);
            Election election = electionService.getElectionData(electionId);

            PoliticalParty foundParty = election.getPoliticalParties().stream()
                    .filter(party -> party.getRegisteredAppellation()
                            .toLowerCase()
                            .contains(partyName.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            if (foundParty != null) {
                return ResponseEntity.ok(foundParty);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error searching for party '{}' in election '{}'. {}", partyName, electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("{electionId}/parties/count")
    public ResponseEntity<Integer> getPartyCount(@PathVariable String electionId) {
        try {
            logger.info("Counting parties for election: {}", electionId);
            int count = electionService.getPoliticalParties(electionId).size();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error counting parties for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("{electionId}/parties/names")
    public ResponseEntity<List<String>> getPartyNames(@PathVariable String electionId) {
        try {
            logger.info("Fetching party names for election: {}", electionId);
            List<String> partyNames = electionService.getPoliticalParties(electionId).stream()
                    .map(PoliticalParty::getRegisteredAppellation)
                    .toList();
            return ResponseEntity.ok(partyNames);
        } catch (Exception e) {
            logger.error("Error fetching party names for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * NOTE: This returns MUNICIPALITY names despite the URL name.
     * Kept here because it functionally belongs to municipality/election data.
     */
    @GetMapping("/kieskirng/names")
    public ResponseEntity<List<String>> getKieskringNames() {
        try {
            logger.info("Fetching all municipality names for default election.");
            List<String> names = electionService.getKiekringName(electionService.loadAllElectionData().getId());
            return ResponseEntity.ok(names);
        } catch (Exception e) {
            logger.error("Error fetching municipality names: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // MOVED: getKieskringNamesOnly -> KieskringController

    @GetMapping("/municipalities/{municipalityName}")
    public ResponseEntity<List<KiesKring>> getMunicipalityResultsByName(@PathVariable String municipalityName) {
        try {
            logger.info("Fetching results for municipality: {}", municipalityName);
            String defaultElectionId = electionService.loadAllElectionData().getId();
            List<KiesKring> results = electionService.getResultsForMunicipality(defaultElectionId, municipalityName);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error fetching municipality results: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // MOVED: getAllProvinces -> ProvinceController

    @GetMapping("/municipalities/all-results")
    public ResponseEntity<KieskringDataDto[]> getAllMunicipalityResults() {
        try {
            logger.info("Loading default election data for combined results...");
            Election election = electionService.loadAllElectionData();
            String electionId = election.getId();

            List<String> municipalityNames = electionService.getKiekringName(electionId);
            List<KieskringDataDto> allKieskringData = new ArrayList<>();

            for (String name : municipalityNames) {
                List<KiesKring> resultsForMuni = electionService.getResultsForMunicipality(electionId, name);
                List<KieskringResultDto> partyResults = resultsForMuni.stream()
                        .map(kieskring -> new KieskringResultDto(
                                kieskring.getPartyName(),
                                kieskring.getValidVotes()
                        ))
                        .toList();

                allKieskringData.add(new KieskringDataDto(name, partyResults));
            }
            return ResponseEntity.ok(allKieskringData.toArray(new KieskringDataDto[0]));

        } catch (Exception e) {
            logger.error("Error fetching all municipality results: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public record KieskringResultDto(String partyName, int validVotes) {}
    public record KieskringDataDto(String name, java.util.List<KieskringResultDto> results) {}
}