package nl.hva.elections.xml.api;

import nl.hva.elections.services.dbPartyService;
import nl.hva.elections.xml.model.Party;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for serving all data related to Political Parties.
 * Refactored to fetch data from the database via dbPartyService.
 */
@RestController
@RequestMapping("/api/parties")
public class PartyController {

    private static final Logger logger = LoggerFactory.getLogger(PartyController.class);

    private final dbPartyService partyService;

    /**
     * Constructs a new PartyController with the database service.
     *
     * @param partyService The service used to retrieve party data from the DB.
     */
    public PartyController(dbPartyService partyService) {
        this.partyService = partyService;
    }

    /**
     * Retrieves all political parties for a specific election from the database.
     * <p>
     * Endpoint: {@code GET /api/parties/{electionId}}
     *
     * @param electionId The unique identifier for the election.
     * @return A {@link ResponseEntity} containing a list of {@link Party} entities.
     */
    @GetMapping("/{electionId}")
    public ResponseEntity<List<Party>> getPoliticalParties(@PathVariable String electionId) {
        try {
            List<Party> parties = partyService.getPartiesByElection(electionId);

            if (parties.isEmpty()) {
                logger.warn("No parties found in database for electionId: {}", electionId);
                return ResponseEntity.notFound().build();
            }

            logger.debug("Total parties fetched from DB: {}\n", parties.size());
            return ResponseEntity.ok(parties);
        } catch (Exception e) {
            logger.error("Internal server error fetching parties for electionId: {}. {}", electionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Finds a specific political party by its name within a given election.
     * <p>
     * Endpoint: {@code GET /api/parties/{electionId}/search?partyName=...}
     *
     * @param electionId The unique identifier for the election.
     * @param partyName  The name of the party to find (passed as a request parameter).
     * @return A {@link ResponseEntity} containing the matching {@link Party}.
     */
    @GetMapping("/{electionId}/search")
    public ResponseEntity<Party> findPartyByName(
            @PathVariable String electionId,
            @RequestParam String partyName) {
        try {
            return partyService.findPartyByName(electionId, partyName)
                    .map(party -> {
                        logger.info("Found party in DB: {}", party.getName());
                        return ResponseEntity.ok(party);
                    })
                    .orElseGet(() -> {
                        logger.warn("Party not found in DB: {}", partyName);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error searching for party '{}' in election '{}'. {}", partyName, electionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Counts the total number of political parties participating in a specific election.
     * <p>
     * Endpoint: {@code GET /api/parties/{electionId}/count}
     *
     * @param electionId The unique identifier for the election.
     * @return A {@link ResponseEntity} containing the total count.
     */
    @GetMapping("/{electionId}/count")
    public ResponseEntity<Integer> getPartyCount(@PathVariable String electionId) {
        try {
            List<Party> parties = partyService.getPartiesByElection(electionId);
            return ResponseEntity.ok(parties.size());
        } catch (Exception e) {
            logger.error("Error counting parties for electionId: {}. {}", electionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a list of all political party names for a specific election.
     * <p>
     * Endpoint: {@code GET /api/parties/{electionId}/names}
     *
     * @param electionId The unique identifier for the election.
     * @return A {@link ResponseEntity} containing a {@link List} of party names.
     */
    @GetMapping("/{electionId}/names")
    public ResponseEntity<List<String>> getPartyNames(@PathVariable String electionId) {
        try {
            List<String> partyNames = partyService.getPartiesByElection(electionId).stream()
                    .map(Party::getName)
                    .toList();
            return ResponseEntity.ok(partyNames);
        } catch (Exception e) {
            logger.error("Error fetching party names for electionId: {}. {}", electionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}