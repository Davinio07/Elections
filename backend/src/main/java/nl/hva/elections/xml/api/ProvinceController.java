package nl.hva.elections.xml.api;

import nl.hva.elections.persistence.model.Kieskring;
import nl.hva.elections.persistence.model.Province;
import nl.hva.elections.repositories.KieskringRepository; // Import needed
import nl.hva.elections.repositories.ProvinceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/provinces")
public class ProvinceController {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceController.class);
    private final ProvinceRepository provinceRepository;
    private final KieskringRepository kieskringRepository; // Inject this

    // Update constructor to include KieskringRepository
    public ProvinceController(ProvinceRepository provinceRepository, KieskringRepository kieskringRepository) {
        this.provinceRepository = provinceRepository;
        this.kieskringRepository = kieskringRepository;
    }

    @GetMapping
    public ResponseEntity<List<Province>> getAllProvinces() {
        try {
            logger.info("Fetching all provinces");
            List<Province> provinces = provinceRepository.findAll();
            return ResponseEntity.ok(provinces);
        } catch (Exception e) {
            logger.error("Error fetching provinces", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- NEW ENDPOINT ---
    @GetMapping("/{id}/kieskringen")
    public ResponseEntity<List<Kieskring>> getKieskringenByProvince(@PathVariable Integer id) {
        try {
            logger.info("Fetching kieskringen for province ID: {}", id);

            // Optional: Check if province exists first, but not strictly necessary for a simple fetch
            if (!provinceRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            List<Kieskring> kieskringen = kieskringRepository.findByProvinceId(id);
            return ResponseEntity.ok(kieskringen);
        } catch (Exception e) {
            logger.error("Error fetching kieskringen for province {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}