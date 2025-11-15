package nl.hva.elections.xml.api;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.PoliticalParty;
import nl.hva.elections.xml.model.Region;
import nl.hva.elections.xml.model.KiesKring;

import nl.hva.elections.xml.service.DutchElectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.hva.elections.persistence.model.Candidate;
import nl.hva.elections.repositories.CandidateRepository;
import nl.hva.elections.repositories.PartyRepository;
import nl.hva.elections.xml.model.Party;

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

    public ElectionController(DutchElectionService electionService, CandidateRepository candidateRepository, PartyRepository partyRepository) {
        this.electionService = electionService;
        this.candidateRepository = candidateRepository;
        this.partyRepository = partyRepository;
    }

    /**
     * Haalt de "standaard" (TK2023) verkiezingsdata op.
     * Is nu een snelle cache-lookup.
     */
    @GetMapping("/results")
    public ResponseEntity<Election> getElectionResults() {
        logger.info("Request received for default election data.");
        Election election = electionService.loadAllElectionData();
        return ResponseEntity.ok(election);
    }

    /**
     * Haalt een specifieke verkiezing op uit de cache.
     */
    @PostMapping("{electionId}")
    public Election readResults(@PathVariable String electionId, @RequestParam(required = false) String folderName) {
        logger.info("Reading results for electionId: {}", electionId);
        return electionService.readResults(electionId, folderName);
    }

    @GetMapping("{electionId}/regions")
    public ResponseEntity<List<Region>> getRegions(@PathVariable String electionId) {
        try {
            logger.info("Fetching regions for election: {}", electionId);
            // Geen parsing meer, haalt direct op uit de service
            List<Region> regions = electionService.getRegions(electionId);
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            logger.error("Error fetching regions for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("{electionId}/regions/kieskringen")
    public ResponseEntity<List<Region>> getKieskringen(@PathVariable String electionId) {
        try {
            logger.info("Fetching kieskringen for election: {}", electionId);
            List<Region> kieskringen = electionService.getKieskringen(electionId);
            return ResponseEntity.ok(kieskringen);
        } catch (Exception e) {
            logger.error("Error fetching kieskringen for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

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

    // --- DB Endpoints (deze waren al in orde) ---

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
            System.out.printf("Fetched %d candidates from DB with filters (partyId: %s, gender: %s).\n",
                    candidates.size(), partyId, gender);
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




    /**
     * Haalt alle gemeentenamen op voor de STANDAARD verkiezing.
     */
    @GetMapping("/municipalities/names")
    public ResponseEntity<List<String>> getMunicipalityNames() {
        try {
            logger.info("Fetching all municipality names for default election.");
            // We gebruiken de standaard (bv. TK2023)
            List<String> names = electionService.getMunicipalityNames(electionService.loadAllElectionData().getId());
            return ResponseEntity.ok(names);
        } catch (Exception e) {
            logger.error("Error fetching municipality names: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Haalt resultaten op voor een specifieke gemeente (voor de STANDAARD verkiezing).
     */
    @GetMapping("/municipalities/{municipalityName}")
    public ResponseEntity<List<KiesKring>> getMunicipalityResultsByName(@PathVariable String municipalityName) {
        try {
            logger.info("Fetching results for municipality: {}", municipalityName);
            // We gebruiken de standaard (bv. TK2023)
            String defaultElectionId = electionService.loadAllElectionData().getId();
            List<KiesKring> results = electionService.getResultsForMunicipality(defaultElectionId, municipalityName);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error fetching municipality results: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Haalt ALLE gemeenteresultaten op (voor de STANDAARD verkiezing).
     * Deze methode was al geoptimaliseerd, maar gebruikt nu de snelle cache-lookup.
     */
    @GetMapping("/municipalities/all-results")
    public ResponseEntity<KieskringDataDto[]> getAllMunicipalityResults() {
        try {
            // 1. Haal de (nu gecachte) data op
            logger.info("Loading default election data for combined results...");
            Election election = electionService.loadAllElectionData();
            String electionId = election.getId(); // bv. "TK2023"

            // 2. Haal de lijst met namen op (gebruikt ook de cache)
            List<String> municipalityNames = electionService.getMunicipalityNames(electionId);

            List<KieskringDataDto> allKieskringData = new ArrayList<>();

            // 4. Loop door de namen
            for (String name : municipalityNames) {

                // 5. Haal resultaten per gemeente op (gebruikt ook de cache)
                List<KiesKring> resultsForMuni = electionService.getResultsForMunicipality(electionId, name);

                // 6. Converteer naar DTO
                List<KieskringResultDto> partyResults = resultsForMuni.stream()
                        .map(kieskring -> new KieskringResultDto(
                                kieskring.getPartyName(),
                                kieskring.getValidVotes()
                        ))
                        .toList();

                allKieskringData.add(new KieskringDataDto(name, partyResults));
            }

            logger.info("Successfully combined all {} municipality results.", allKieskringData.size());
            return ResponseEntity.ok(allKieskringData.toArray(new KieskringDataDto[0])); // Converteer naar Array

        } catch (Exception e) {
            logger.error("Error fetching all municipality results: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DTO records (kunnen hier blijven of in een eigen bestand)
    public record KieskringResultDto(String partyName, int validVotes) {}
    public record KieskringDataDto(String name, java.util.List<KieskringResultDto> results) {}
}