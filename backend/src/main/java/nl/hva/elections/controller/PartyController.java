package nl.hva.elections.controller;

import nl.hva.elections.xml.model.Party;
import nl.hva.elections.xml.service.PartyService;
import nl.hva.elections.xml.utils.xml.transformers.PartyTransformer;
import java.util.List;

// Import necessary Spring Web annotations
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller layer for the political parties API.
 * * @RestController: Marks this class as a Spring component that handles HTTP requests
 * and automatically serializes the returned objects (like List<Party>) to JSON.
 * @RequestMapping: Defines the base URL path for all methods in this controller.
 */
@RestController
@RequestMapping("/api/v1/parties")
public class PartyController {

    private final PartyService partyService;

    /**
     * Constructor Injection: Spring Boot automatically detects this constructor
     * and injects the required PartyService instance (assuming PartyService
     * is also marked as a Spring component, e.g., with @Service).
     * * NOTE: In a complete Spring setup, the Transformer would be injected into
     * the Service, and the Service would be injected here.
     * * Since we only have the Service constructor taking a Transformer, we will
     * assume the Service and Transformer are configured as beans for simplicity.
     */
    // For this example, we'll keep the manual instantiation for a self-contained environment,
    // but note that in Spring, you'd typically have @Autowired or just rely on constructor injection.
    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    /**
     * Defines the REST API Endpoint for retrieving the list of parties.
     * * Endpoint: GET /api/v1/parties
     * * @GetMapping: Maps HTTP GET requests to this method. If no path is specified,
     * it uses the base path defined in @RequestMapping ("/api/v1/parties").
     * @return The list of Party models (automatically serialized to JSON).
     */
    @GetMapping
    public List<Party> getPartiesList() {
        // The service layer handles the core business logic (data fetching/transformation)
        return partyService.getPoliticalParties();
    }

    // The static main method is removed, as Spring Boot's application starter
    // now manages the application lifecycle.
}
