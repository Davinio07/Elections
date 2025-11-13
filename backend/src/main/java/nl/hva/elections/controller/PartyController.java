package nl.hva.elections.xml.controller;

import nl.hva.elections.xml.model.PoliticalParty;
import nl.hva.elections.xml.service.PartyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller specifiek voor het beheren van API endpoints
 * die gerelateerd zijn aan politieke partijen.
 */
@RestController
@RequestMapping("/api/parties") // Een duidelijke base-path voor partijen
public class PartyController {

    private final PartyService partyService;

    // Injecteer de NIEUWE, specifieke PartyService
    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    /**
     * Haalt alle partijen op voor een gegeven verkiezing.
     * Endpoint: GET /api/parties?electionId=TK2023
     */
    @GetMapping
    public List<PoliticalParty> getParties(
            @RequestParam(defaultValue = "TK2023") String electionId) {

        // Delegeer al het werk naar de service
        return partyService.getPoliticalParties(electionId);
    }

    // Hier zouden andere partij-specifieke endpoints komen,
    // zoals /api/parties/{id}
}