package nl.hva.elections.xml.api;

import nl.hva.elections.xml.model.Candidate;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.NationalResult;
import nl.hva.elections.xml.model.Region;
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

    // ===================================================================================
    // NIEUWE METHODE VOOR DE FRONTEND (MINIMALE TOEVOEGING)
    // ===================================================================================

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


    // ===================================================================================
    // ALLE ORIGINELE METHODES ZIJN HIERONDER BEHOUDEN
    // ===================================================================================

    /**
     * Processes the result for a specific election.
     * @param electionId the id of the election, e.g. the value of the Id attribute from the ElectionIdentifier tag.
     * @param folderName the name of the folder that contains the XML result files. If none is provided the value from
     *                   the electionId is used.
     * @return Election if the results have been processed successfully. Please be sure yoy don't output all the data!
     * Just the general data about the election should be sent back to the front-end!<br/>
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
}