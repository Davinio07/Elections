package nl.hva.elections.xml.api;

import nl.hva.elections.services.dbPartyService;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.PoliticalParty;
import nl.hva.elections.xml.model.Region;
import nl.hva.elections.xml.model.NationalResult;
import nl.hva.elections.xml.model.KiesKring; // Needed for getMunicipalityResultsByName

import nl.hva.elections.xml.service.DutchElectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// JPA/Database models (These are required for the new database endpoint)
import nl.hva.elections.persistence.model.Candidate;
import nl.hva.elections.repositories.CandidateRepository;
import nl.hva.elections.repositories.PartyRepository;
import nl.hva.elections.persistence.model.Party;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Demo controller for showing how you could load the election data in the backend.
 */
@RestController
@RequestMapping("/api/elections")
public class ElectionController {

    private static final Logger logger = LoggerFactory.getLogger(ElectionController.class);
    private final DutchElectionService electionService;
    private final CandidateRepository candidateRepository; // <-- Used for new DB endpoint
    private final dbPartyService dbPartyService;
    private final PartyRepository partyRepository;
    private List<Region> regions = new ArrayList<>();

    // Constructor updated to inject CandidateRepository
    public ElectionController(DutchElectionService electionService, CandidateRepository candidateRepository, dbPartyService dbPartyService, PartyRepository partyRepository) {
        this.electionService = electionService;
        this.candidateRepository = candidateRepository;
        this.dbPartyService = dbPartyService;
        this.partyRepository = partyRepository;
    }

    /**
     * Retrieves all election data in one fell swoop.
     * This is the endpoint used by the front end.
     */
    @GetMapping("/results")
    public ResponseEntity<Election> getElectionResults() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        logger.info("Request received to load all election data.");
        Election election = electionService.loadAllElectionData();
        logger.info("Successfully loaded all election data.");
        return ResponseEntity.ok(election);
    }

    /**
     * Processes the result for a specific election.
     * @param electionId the id of the election, e.g. the value of the Id attribute from the ElectionIdentifier tag.
     * @param folderName the name of the folder that contains the XML result files. If none is provided the value from
     * the electionId is used.
     * @return Election if the results have been processed successfully. Please be sure you don't output all the data!
     * Just the general data about the election should be sent back to the front-end!

     * <i>If you want to return something else please feel free to do so!</i>
     */
    @PostMapping("{electionId}")
    public Election readResults(@PathVariable String electionId, @RequestParam(required = false) String folderName) {
        logger.info("Reading results for electionId: {} with folderName: {}", electionId, folderName);
        if (folderName == null) {
            return electionService.readResults(electionId, electionId);
        } else {
            return electionService.readResults(electionId, folderName);
        }
    }

    public void addRegion(Region region) {
        this.regions.add(region);
    }

    public List<Region> getRegions() {
        return regions;
    }

    @GetMapping("{electionId}/regions")
    public List<Region> getRegions(@PathVariable String electionId,
                                   @RequestParam(required = false) String folderName) {
        try {
            logger.info("Fetching regions for election: {}", electionId);
            Election election = (folderName == null)
                    ? electionService.readResults(electionId, electionId)
                    : electionService.readResults(electionId, folderName);

            return election.getRegions();
        } catch (Exception e) {
            logger.error("Error fetching regions for electionId: {}. Returning empty list.", electionId, e);
            return Collections.emptyList();
        }
    }

    @GetMapping("{electionId}/regions/kieskringen")
    public List<Region> getKieskringen(@PathVariable String electionId,
                                       @RequestParam(required = false) String folderName) {
        logger.info("Fetching kieskringen for election: {}", electionId);
        Election election = (folderName == null)
                ? electionService.readResults(electionId, electionId)
                : electionService.readResults(electionId, folderName);

        return electionService.getKieskringen(election);
    }

    /**
     * Get a list of all municipalities (gemeenten).
     * @param electionId The election identifier.
     * @param folderName Optional folder name.
     * @return A list of regions with the category 'GEMEENTE'.
     */
    @GetMapping("{electionId}/regions/gemeenten")
    public List<Region> getGemeenten(@PathVariable String electionId,
                                     @RequestParam(required = false) String folderName) {
        logger.info("Fetching gemeenten for election: {}", electionId);
        Election election = (folderName == null)
                ? electionService.readResults(electionId, electionId)
                : electionService.readResults(electionId, folderName);

        return electionService.getGemeenten(election);
    }

    @GetMapping("{electionId}/national")
    public ResponseEntity<List<NationalResult>> getNationalResults(@PathVariable String electionId) {
        logger.info("Fetching national results for election: {}", electionId);
        List<NationalResult> results = electionService.getNationalResults(electionId);
        return ResponseEntity.ok(results);
    }

    /* * The old getCandidates method has been removed to resolve the incompatible types error.
     * You should now use the new /candidates/db endpoint to read data from the database.
     */
    // @GetMapping("{electionId}/candidates") - OLD METHOD REMOVED!

    /**
     * Endpoint to get all candidates directly from the database (JPA model), with optional filters.
     * This replaces the previous /candidates/db method.
     */
    @GetMapping("/candidates/db")
    public ResponseEntity<List<Candidate>> getAllCandidatesFromDb(
            @RequestParam(required = false) Long partyId,
            @RequestParam(required = false) String gender) { // <-- NEW PARAMETERS
        try {
            List<Candidate> candidates;

            // Logic to select the correct JPA repository method based on presence of filters
            if (partyId != null && gender != null) {
                candidates = candidateRepository.findByPartyIdAndGender(partyId, gender);
            } else if (partyId != null) {
                candidates = candidateRepository.findByPartyId(partyId);
            } else if (gender != null) {
                candidates = candidateRepository.findByGender(gender);
            } else {
                // No filters applied, return all
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

    /**
     * Gets all political parties from the database with their votes and seats.
     * Replaces XML-based national results for frontend charts.
     */
    @GetMapping("/{electionId}/parties/db")
    public ResponseEntity<List<Party>> getPartiesByElection(@PathVariable String electionId) {
        List<Party> parties = dbPartyService.getPartiesByElection(electionId);
        if (parties.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(parties);
    }


    @GetMapping("/test-election") // Changed path to be more descriptive
    public ResponseEntity<List<Party>> testForRepo(
            @RequestParam("id") String electionId) {

        // 3. Call your repository method
        List<Party> parties = partyRepository.findByElectionId(electionId);

        // 4. Return the result
        // Spring will automatically convert this List<Party> into JSON
        return ResponseEntity.ok(parties);
    }
    /**
     * Get all political parties for a specific election.
     *
     * @param electionId the election identifier (e.g., "TK2023")
     * @param folderName optional folder name
     * @return list of political parties
     */
    @GetMapping("{electionId}/parties")
    public ResponseEntity<List<PoliticalParty>> getPoliticalParties(
            @PathVariable String electionId,
            @RequestParam(required = false) String folderName) {

        try {
            logger.info("Fetching political parties for election: {}", electionId);
            Election election = (folderName == null)
                    ? electionService.readResults(electionId, electionId)
                    : electionService.readResults(electionId, folderName);

            List<PoliticalParty> parties = election.getPoliticalParties();
            logger.debug("Total parties: {}\n", parties.size());

            return ResponseEntity.ok(parties);
        } catch (Exception e) {
            logger.error("Error fetching parties for electionId: {}. Returning empty list.", electionId, e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * Get a specific political party by its name.
     *
     * @param electionId the election identifier
     * @param partyName the party name to search for
     * @param folderName optional folder name
     * @return the political party if found
     */
    @GetMapping("{electionId}/parties/search")
    public ResponseEntity<PoliticalParty> findPartyByName(
            @PathVariable String electionId,
            @RequestParam String partyName,
            @RequestParam(required = false) String folderName) {

        try {
            logger.info("Searching for party '{}' in election '{}'", partyName, electionId);
            Election election = (folderName == null)
                    ? electionService.readResults(electionId, electionId)
                    : electionService.readResults(electionId, folderName);

            PoliticalParty foundParty = election.getPoliticalParties().stream()
                    .filter(party -> party.getRegisteredAppellation()
                            .toLowerCase()
                            .contains(partyName.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            if (foundParty != null) {
                logger.info("Found party: {}", foundParty.getRegisteredAppellation());
                return ResponseEntity.ok(foundParty);
            } else {
                logger.warn("Party not found: {}", partyName);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error searching for party '{}' in election '{}'", partyName, electionId, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get the count of political parties.
     *
     * @param electionId the election identifier
     * @param folderName optional folder name
     * @return count of parties
     */
    @GetMapping("{electionId}/parties/count")
    public ResponseEntity<Integer> getPartyCount(
            @PathVariable String electionId,
            @RequestParam(required = false) String folderName) {

        try {
            logger.info("Counting parties for election: {}", electionId);
            Election election = (folderName == null)
                    ? electionService.readResults(electionId, electionId)
                    : electionService.readResults(electionId, folderName);

            int count = election.getPoliticalParties().size();
            logger.info("Total parties for {}: {}", electionId, count);

            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error counting parties for electionId: {}. Returning empty list.", electionId, e);
            return ResponseEntity.ok(0);
        }
    }

    /**
     * Get all party names as a simple list of strings.
     * Useful for dropdowns or autocomplete features.
     *
     * @param electionId the election identifier
     * @param folderName optional folder name
     * @return list of party names
     */
    @GetMapping("{electionId}/parties/names")
    public ResponseEntity<List<String>> getPartyNames(
            @PathVariable String electionId,
            @RequestParam(required = false) String folderName) {

        try {
            logger.info("Fetching party names for election: {}", electionId);
            Election election = (folderName == null)
                    ? electionService.readResults(electionId, electionId)
                    : electionService.readResults(electionId, folderName);

            List<String> partyNames = election.getPoliticalParties().stream()
                    .map(PoliticalParty::getRegisteredAppellation)
                    .toList();

            return ResponseEntity.ok(partyNames);
        } catch (Exception e) {
            logger.error("Error fetching party names for electionId: {}. Returning empty list.", electionId, e);
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * Gets all unique municipality names for the election
     * Used for the search dropdown in the frontend
     * @return A response entity containing a list of municipality names
     */
    @GetMapping("/municipalities/names")
    public ResponseEntity<List<String>> getMunicipalityNames() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        logger.info("Fetching all municipality names.");
        Election election = electionService.loadAllElectionData();
        List<String> names = electionService.getMunicipalityNames(election);
        return ResponseEntity.ok(names);
    }

    /**
     * Gets the election results for a specific municipality.
     * @param municipalityName The name of the municipality from the URL path.
     * @return A response entity containing a list of results for that municipality.
     */
    @GetMapping("/municipalities/{municipalityName}")
    public ResponseEntity<List<KiesKring>> getMunicipalityResultsByName(@PathVariable String municipalityName) throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        logger.info("Fetching results for municipality: {}", municipalityName);
        Election election = electionService.loadAllElectionData();
        List<KiesKring> results = electionService.getResultsForMunicipality(election, municipalityName);
        return ResponseEntity.ok(results);
    }

    /**
     * This is a web API endpoint that shows the final seat allocation for a specific election.
     *
     * @param electionId The unique identifier for the election, passed in the URL path.
     * @return A {@code ResponseEntity} containing a map where the key is the party name
     * and the value is the number of seats allocated. It returns an HTTP 200 (OK)
     * status on successful retrieval and calculation.
     */
    @GetMapping("{electionId}/seats")
    public ResponseEntity<Map<String, Integer>> getSeats(@PathVariable String electionId) {
        List<NationalResult> results = electionService.getNationalResults(electionId);
        Map<String, Integer> seats = electionService.calculateSeats(results, 150);
        return ResponseEntity.ok(seats);
    }
    // Inside your ElectionController.java

// ... (other methods) ...

    /**
     * Gets ALL municipality results in one single call.
     * This avoids the N+1 problem on the frontend.
     */
    @GetMapping("/municipalities/all-results")
    public ResponseEntity<List<KieskringDataDto>> getAllMunicipalityResults() {
        try {
            // 1. Load the XML data ONCE
            logger.info("Loading all election data for combined results...");
            Election election = electionService.loadAllElectionData();

            // 2. Get the list of all municipality names
            List<String> municipalityNames = electionService.getMunicipalityNames(election);

            // 3. This will be our final list
            List<KieskringDataDto> allKieskringData = new ArrayList<>();

            // 4. Loop through the names ON THE SERVER
            for (String name : municipalityNames) {

                // 5. Get results for one municipality
                //    This method MUST use the 'election' object we already loaded,
                //    so it doesn't re-parse the XML!
                List<KiesKring> resultsForMuni = electionService.getResultsForMunicipality(election, name);

                // 6. Convert the results to our simple DTO
                //    (I am assuming 'KiesKring' has getPartyName() and getValidVotes())
                List<KieskringResultDto> partyResults = resultsForMuni.stream()
                        .map(kieskring -> new KieskringResultDto(
                                kieskring.getPartyName(), // You might need to adjust this
                                kieskring.getValidVotes() // You might need to adjust this
                        ))
                        .toList();

                // 7. Add the combined data to our main list
                allKieskringData.add(new KieskringDataDto(name, partyResults));
            }

            logger.info("Successfully combined all {} municipality results.", allKieskringData.size());
            return ResponseEntity.ok(allKieskringData);

        } catch (Exception e) {
            logger.error("Error fetching all municipality results: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // You can use "record" as a simple way to create these DTOs
// This matches your frontend's "KieskringResult"
    public record KieskringResultDto(String partyName, int validVotes) {}

    // This matches your frontend's "KieskringData"
    public record KieskringDataDto(String name, java.util.List<KieskringResultDto> results) {}
}