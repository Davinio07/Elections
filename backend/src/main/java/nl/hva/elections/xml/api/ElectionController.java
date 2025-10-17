package nl.hva.elections.xml.api;

import nl.hva.elections.xml.model.Candidate;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.MunicipalityResult;
import nl.hva.elections.xml.model.PoliticalParty;
import nl.hva.elections.xml.model.Region;
import nl.hva.elections.xml.model.NationalResult;
import nl.hva.elections.xml.service.DutchElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Demo controller for showing how you could load the election data in the backend.
 */
@RestController
@RequestMapping("/api/elections")
public class ElectionController {
    private final DutchElectionService electionService;
    private List<Region> regions = new ArrayList<>();

    public ElectionController(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     * Haalt alle verkiezingsdata in één keer op.
     * Dit is de endpoint die je frontend gebruikt.
     */
    @GetMapping("/results")
    public ResponseEntity<Election> getElectionResults() {
        try {
            // Roep de service aan die alle data laadt en parset
            Election election = electionService.loadAllElectionData();
            // Stuur de data terug met een 200 OK status
            return ResponseEntity.ok(election);
        } catch (IOException | XMLStreamException | ParserConfigurationException | SAXException e) {
            // Als er iets misgaat, print de error en stuur een 500 Internal Server Error status
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Processes the result for a specific election.
     * @param electionId the id of the election, e.g. the value of the Id attribute from the ElectionIdentifier tag.
     * @param folderName the name of the folder that contains the XML result files. If none is provided the value from
     *                   the electionId is used.
     * @return Election if the results have been processed successfully. Please be sure you don't output all the data!
     * Just the general data about the election should be sent back to the front-end!

     * <i>If you want to return something else please feel free to do so!</i>
     */
    @PostMapping("{electionId}")
    public Election readResults(@PathVariable String electionId, @RequestParam(required = false) String folderName) {
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
            Election election = (folderName == null)
                    ? electionService.readResults(electionId, electionId)
                    : electionService.readResults(electionId, folderName);

            return election.getRegions();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @GetMapping("{electionId}/regions/kieskringen")
    public List<Region> getKieskringen(@PathVariable String electionId,
                                       @RequestParam(required = false) String folderName) {
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
        Election election = (folderName == null)
                ? electionService.readResults(electionId, electionId)
                : electionService.readResults(electionId, folderName);

        return electionService.getGemeenten(election);
    }

    @GetMapping("{electionId}/national")
    public ResponseEntity<List<NationalResult>> getNationalResults(@PathVariable String electionId) {
        List<NationalResult> results = electionService.getNationalResults(electionId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("{electionId}/candidates")
    public List<Candidate> getCandidates(@PathVariable String electionId,
                                         @RequestParam(required = false) String folderName) {
        try {
            Election election = (folderName == null)
                    ? electionService.readResults(electionId, electionId)
                    : electionService.readResults(electionId, folderName);
            return (election == null) ? Collections.emptyList() : election.getCandidates();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
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
            Election election = (folderName == null)
                    ? electionService.readResults(electionId, electionId)
                    : electionService.readResults(electionId, folderName);

            List<PoliticalParty> parties = election.getPoliticalParties();

            // Print to console
            System.out.println("\n=== Political Parties for " + electionId + " ===");
            parties.forEach(party ->
                    System.out.println("- " + party.getRegisteredAppellation())
            );
            System.out.println("Total parties: " + parties.size() + "\n");

            return ResponseEntity.ok(parties);
        } catch (Exception e) {
            e.printStackTrace();
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
                System.out.println("Found party: " + foundParty.getRegisteredAppellation());
                return ResponseEntity.ok(foundParty);
            } else {
                System.out.println("Party not found: " + partyName);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            Election election = (folderName == null)
                    ? electionService.readResults(electionId, electionId)
                    : electionService.readResults(electionId, folderName);

            int count = election.getPoliticalParties().size();
            System.out.println("Total parties for " + electionId + ": " + count);

            return ResponseEntity.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
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
            Election election = (folderName == null)
                    ? electionService.readResults(electionId, electionId)
                    : electionService.readResults(electionId, folderName);

            List<String> partyNames = election.getPoliticalParties().stream()
                    .map(PoliticalParty::getRegisteredAppellation)
                    .toList();

            return ResponseEntity.ok(partyNames);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    /**
     * Gets all unique municipality names for the election
     * Used for the search dropdown in the frontend
     * @return A response entity containing a list of municipality names
     */
    @GetMapping("/municipalities/names")
    public ResponseEntity<List<String>> getMunicipalityNames() {
        try {
            Election election = electionService.loadAllElectionData();
            List<String> names = electionService.getMunicipalityNames(election);
            return ResponseEntity.ok(names);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Gets the election results for a specific municipality.
     * @param municipalityName The name of the municipality from the URL path.
     * @return A response entity containing a list of results for that municipality.
     */
    @GetMapping("/municipalities/{municipalityName}")
    public ResponseEntity<List<MunicipalityResult>> getMunicipalityResultsByName(@PathVariable String municipalityName) {
        try {
            Election election = electionService.loadAllElectionData();
            List<MunicipalityResult> results = electionService.getResultsForMunicipality(election, municipalityName);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}