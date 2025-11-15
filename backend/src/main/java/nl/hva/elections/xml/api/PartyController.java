package nl.hva.elections.xml.api;

import nl.hva.elections.xml.model.PoliticalParty;
import nl.hva.elections.xml.service.PartyService;
import nl.hva.elections.exception.ElectionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for serving all data related to Political Parties
 * from the XML cache.
 */
@RestController
@RequestMapping("/api/parties")
public class PartyController {

    private static final Logger logger = LoggerFactory.getLogger(PartyController.class);

    private final PartyService partyService;

    /**
     * Constructs a new PartyController with the necessary PartyService.
     *
     * @param partyService The service used to retrieve party data.
     */
    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    /**
     * Retrieves all political parties for a specific election.
     * <p>
     * Endpoint: {@code GET /api/parties/{electionId}}
     *
     * @param electionId The unique identifier for the election.
     * @return A {@link ResponseEntity} containing a list of {@link PoliticalParty} objects.
     * Returns {@link HttpStatus#OK} (200) with the list of parties if found.
     * Returns {@link HttpStatus#NOT_FOUND} (404) if the election ID is invalid.
     * Returns {@link HttpStatus#INTERNAL_SERVER_ERROR} (500) for unexpected server errors.
     */
    @GetMapping("/{electionId}")
    public ResponseEntity<List<PoliticalParty>> getPoliticalParties(@PathVariable String electionId) {
        try {
            List<PoliticalParty> parties = partyService.getPoliticalParties(electionId);
            logger.debug("Total parties: {}\n", parties.size());
            return ResponseEntity.ok(parties);
        } catch (ElectionNotFoundException e) {
            logger.warn("No election data found for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
     * @return A {@link ResponseEntity} containing the matching {@link PoliticalParty}.
     * Returns {@link HttpStatus#OK} (200) with the party if found.
     * Returns {@link HttpStatus#NOT_FOUND} (404) if the party or election is not found.
     * Returns {@link HttpStatus#INTERNAL_SERVER_ERROR} (500) for unexpected server errors.
     */
    @GetMapping("/{electionId}/search")
    public ResponseEntity<PoliticalParty> findPartyByName(
            @PathVariable String electionId,
            @RequestParam String partyName) {
        try {
            return partyService.findPartyByName(electionId, partyName)
                    .map(party -> {
                        logger.info("Found party: {}", party.getRegisteredAppellation());
                        return ResponseEntity.ok(party);
                    })
                    .orElseGet(() -> {
                        logger.warn("Party not found: {}", partyName);
                        return ResponseEntity.notFound().build();
                    });
        } catch (ElectionNotFoundException e) {
            logger.warn("No election data found for electionId: {} when searching for party: {}. {}", electionId, partyName, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
     * @return A {@link ResponseEntity} containing the total count of parties as an {@link Integer}.
     * Returns {@link HttpStatus#OK} (200) with the count.
     * Returns {@link HttpStatus#NOT_FOUND} (404) if the election ID is invalid.
     * Returns {@link HttpStatus#INTERNAL_SERVER_ERROR} (500) for unexpected server errors.
     */
    @GetMapping("/{electionId}/count")
    public ResponseEntity<Integer> getPartyCount(@PathVariable String electionId) {
        try {
            int count = partyService.getPartyCount(electionId);
            return ResponseEntity.ok(count);
        } catch (ElectionNotFoundException e) {
            logger.warn("No election data found for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
     * @return A {@link ResponseEntity} containing a {@link List} of party names ({@link String}).
     * Returns {@link HttpStatus#OK} (200) with the list of names.
     * Returns {@link HttpStatus#NOT_FOUND} (404) if the election ID is invalid.
     * Returns {@link HttpStatus#INTERNAL_SERVER_ERROR} (500) for unexpected server errors.
     */
    @GetMapping("/{electionId}/names")
    public ResponseEntity<List<String>> getPartyNames(@PathVariable String electionId) {
        try {
            List<String> partyNames = partyService.getPartyNames(electionId);
            return ResponseEntity.ok(partyNames);
        } catch (ElectionNotFoundException e) {
            logger.warn("No election data found for electionId: {}. {}", electionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error fetching party names for electionId: {}. {}", electionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}