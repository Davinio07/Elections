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

@RestController
@RequestMapping("/api/kieskring")
public class KieskringController {

    private static final Logger logger = LoggerFactory.getLogger(KieskringController.class);

    private final KieskringRepository kieskringRepository;
    private final DutchElectionService electionService;
    private final GemeenteRepository gemeenteRepository; // <--- Inject this

    public KieskringController(KieskringRepository kieskringRepository, GemeenteRepository gemeenteRepository , DutchElectionService electionService) {
        this.kieskringRepository = kieskringRepository;
        this.electionService = electionService;
        this.gemeenteRepository = gemeenteRepository;
    }

    /**
     * Gets all Kieskring names from the Database.
     * Endpoint moved and renamed to be cleaner.
     */
    @GetMapping("/names/db")
    public ResponseEntity<List<String>> getKieskringNamesOnly() {
        try {
            logger.info("Fetching all Kieskring names from database.");
            List<String> names = kieskringRepository.findAllByOrderByNameAsc().stream()
                    .map(Kieskring::getName)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(names);
        } catch (Exception e) {
            logger.error("Error fetching Kieskring names: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets Kieskring regions from the XML service.
     */
    @GetMapping("/{electionId}")
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

    @GetMapping("/{id}/gemeentes")
    public ResponseEntity<List<Gemeente>> getGemeentesByKieskring(@PathVariable Integer id) {
        try {
            logger.info("Fetching gemeentes for kieskring ID: {}", id);

            if (!kieskringRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            List<Gemeente> gemeentes = gemeenteRepository.findByKieskringId(id);
            return ResponseEntity.ok(gemeentes);
        } catch (Exception e) {
            logger.error("Error fetching gemeentes for kieskring {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}