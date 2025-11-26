package nl.hva.elections.controllers;

import nl.hva.elections.models.Party;
import nl.hva.elections.Service.dbPartyService;
import nl.hva.elections.Service.NationalResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller responsible for serving calculated results and
 * data retrieved from the database.
 */
@RestController
@RequestMapping("/api/nationalResult")
public class NationalResultController {

    private static final Logger logger = LoggerFactory.getLogger(NationalResultController.class);

    private final NationalResultService nationalResultService;
    private final dbPartyService dbPartyService;

    public NationalResultController(NationalResultService nationalResultService, dbPartyService dbPartyService) {
        this.nationalResultService = nationalResultService;
        this.dbPartyService = dbPartyService;
    }

    /**
     * Retrieves the raw national results (votes) from the cached XML data.
     */
    @GetMapping("/{electionId}/national") // Becomes: /api/nationalResult/{electionId}/national
    public ResponseEntity<List<Party>> getNationalResults(@PathVariable String electionId) {
        List<Party> parties = dbPartyService.getPartiesByElection(electionId);
        if (parties.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(parties);
    }


    /**
     * Calculates and returns the seat distribution based on national results.
     */
    @GetMapping("/{electionId}/seats") // Becomes: /api/nationalResult/{electionId}/seats
    public ResponseEntity<Map<String, Integer>> getSeats(@PathVariable String electionId) {
        try {
            List<Party> results = nationalResultService.getNationalResults(electionId);
            Map<String, Integer> seats = nationalResultService.calculateSeats(results, 150);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            logger.error("Error calculating seats for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}