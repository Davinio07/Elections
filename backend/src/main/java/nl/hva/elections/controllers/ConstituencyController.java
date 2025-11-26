package nl.hva.elections.controllers;

import nl.hva.elections.dtos.KieskringDTO;
import nl.hva.elections.models.Gemeente;
import nl.hva.elections.models.Kieskring;
import nl.hva.elections.models.Region;
import nl.hva.elections.repositories.GemeenteRepository;
import nl.hva.elections.repositories.KieskringRepository;
import nl.hva.elections.service.DutchElectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for serving Constituency (Kieskring) data.
 * Refactored to use GlobalExceptionHandler for error logging and handling.
 */
@RestController
@RequestMapping("/api/constituencies")
public class ConstituencyController {

    private static final Logger logger = LoggerFactory.getLogger(ConstituencyController.class);

    private final KieskringRepository kieskringRepository;
    private final GemeenteRepository gemeenteRepository;
    private final DutchElectionService electionService;

    public ConstituencyController(KieskringRepository kieskringRepository,
                                  GemeenteRepository gemeenteRepository,
                                  DutchElectionService electionService) {
        this.kieskringRepository = kieskringRepository;
        this.gemeenteRepository = gemeenteRepository;
        this.electionService = electionService;
    }

    /**
     * Gets all constituency (kieskring) names from the Database.
     * Exceptions are handled by GlobalExceptionHandler.
     */
    @GetMapping("/names/db")
    public ResponseEntity<List<String>> getConstituencyNamesOnly() {
        logger.info("Fetching all Constituency names from database.");
        List<String> names = kieskringRepository.findAllByOrderByNameAsc().stream()
                .map(Kieskring::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(names);
    }

    /**
     * Gets all constituencies from the Database as DTOs.
     * Exceptions are handled by GlobalExceptionHandler.
     */
    @GetMapping("/db")
    public ResponseEntity<List<KieskringDTO>> getAllConstituenciesFromDB() {
        logger.info("Fetching all Constituencies as DTOs from database.");

        // Get the raw Entities (Models)
        List<Kieskring> entities = kieskringRepository.findAllByOrderByNameAsc();

        // Convert Entities to DTOs
        List<KieskringDTO> dtos = entities.stream()
                .map(k -> new KieskringDTO(
                        k.getKieskring_id(),
                        k.getName(),
                        // Handle null province just in case
                        k.getProvince() != null ? k.getProvince().getName() : "Unknown"
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Gets constituency (kieskring) regions from the XML service.
     * Exceptions are handled by GlobalExceptionHandler.
     */
    @GetMapping("/{electionId}")
    public ResponseEntity<List<Region>> getConstituencies(@PathVariable String electionId) {
        logger.info("Fetching constituencies for election: {}", electionId);
        List<Region> constituencies = electionService.getConstituencies(electionId);

        if (constituencies == null || constituencies.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(constituencies);
    }

    /**
     * Gets municipalities for a specific constituency ID.
     * Essential for the Province -> Constituency -> Municipality drill-down.
     */
    @GetMapping("/{id}/municipalities")
    public ResponseEntity<List<Gemeente>> getMunicipalitiesByConstituency(@PathVariable Integer id) {
        logger.info("Fetching municipalities for constituency ID: {}", id);

        // return 404.
        if (!kieskringRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<Gemeente> gemeentes = gemeenteRepository.findByKieskringId(id);
        return ResponseEntity.ok(gemeentes);
    }

    /**
     * Returns the aggregated RESULTS for all constituencies.
     * Exceptions are handled by GlobalExceptionHandler.
     */
    @GetMapping("/results")
    public ResponseEntity<List<DutchElectionService.ConstituencyResultDto>> getAllConstituencyResults() {
        logger.info("Calculating and fetching aggregated constituency results.");

        // We use the default election ID for now
        List<DutchElectionService.ConstituencyResultDto> results =
                electionService.getAggregatedConstituencyResults("TK2023");

        return ResponseEntity.ok(results);
    }
}