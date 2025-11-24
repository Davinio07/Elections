package nl.hva.elections.xml.api;

import nl.hva.elections.persistence.model.Gemeente;
import nl.hva.elections.persistence.model.Kieskring;
import nl.hva.elections.repositories.GemeenteRepository;
import nl.hva.elections.repositories.KieskringRepository;
import nl.hva.elections.xml.model.Region;
import nl.hva.elections.xml.service.DutchElectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for serving Constituency (Kieskring) data.
 * This handles both the STRUCTURE (finding municipalities in a constituency)
 * and the RESULTS (votes aggregated per constituency).
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
     * Used for dropdowns or lists.
     */
    @GetMapping("/names/db")
    public ResponseEntity<List<String>> getConstituencyNamesOnly() {
        try {
            logger.info("Fetching all Constituency names from database.");
            List<String> names = kieskringRepository.findAllByOrderByNameAsc().stream()
                    .map(Kieskring::getName)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(names);
        } catch (Exception e) {
            logger.error("Error fetching Constituency names: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets constituency (kieskring) regions from the XML service.
     */
    @GetMapping("/{electionId}")
    public ResponseEntity<List<Region>> getConstituencies(@PathVariable String electionId) {
        try {
            logger.info("Fetching constituencies for election: {}", electionId);
            // Note: electionService.getConstituencies needs to be updated or alias getKieskringen
            List<Region> constituencies = electionService.getConstituencies(electionId);
            return ResponseEntity.ok(constituencies);
        } catch (Exception e) {
            logger.error("Error fetching constituencies for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Gets municipalities for a specific constituency ID.
     * Essential for the Province -> Constituency -> Municipality drill-down.
     */
    @GetMapping("/{id}/municipalities")
    public ResponseEntity<List<Gemeente>> getMunicipalitiesByConstituency(@PathVariable Integer id) {
        try {
            logger.info("Fetching municipalities for constituency ID: {}", id);

            if (!kieskringRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            List<Gemeente> gemeentes = gemeenteRepository.findByKieskringId(id);
            return ResponseEntity.ok(gemeentes);
        } catch (Exception e) {
            logger.error("Error fetching municipalities for constituency {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Returns the aggregated RESULTS for all constituencies.
     * This calculates the sum of all municipalities belonging to a constituency.
     */
    @GetMapping("/results")
    public ResponseEntity<List<DutchElectionService.ConstituencyResultDto>> getAllConstituencyResults() {
        try {
            logger.info("Calculating and fetching aggregated constituency results.");
            // We use the default election ID for now
            List<DutchElectionService.ConstituencyResultDto> results = 
                electionService.getAggregatedConstituencyResults("TK2023");
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            logger.error("Error calculating constituency results: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}