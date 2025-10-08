package nl.hva.elections.xml.api;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.Region;
import nl.hva.elections.xml.service.DutchElectionService;
import org.springframework.web.bind.annotation.*;

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
     * Processes the result for a specific election.
     * @param electionId the id of the election, e.g. the value of the Id attribute from the ElectionIdentifier tag.
     * @param folderName the name of the folder that contains the XML result files. If none is provided the value from
     *                   the electionId is used.
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
}
