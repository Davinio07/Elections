package nl.hva.elections.xml.service;

import nl.hva.elections.xml.model.Party;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for handling logic related to national results.
 * This includes fetching the results from the XML cache and performing
 * calculations like seat distribution (D'Hondt).
 */
@Service
public class NationalResultService {

    /**
     * The service that holds the cached, pre-parsed XML data.
     */
    private final DutchElectionService electionService;

    /**
     * Constructs the service, injecting the XML data cache.
     *
     * @param electionService The service holding all cached Election objects.
     */
    public NationalResultService(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     * Retrieves the list of NationalResult objects for a specific election
     * from the central election data cache.
     *
     * @param electionId The identifier of the election (e.g., "TK2023").
     * @return A list of NationalResult objects.
     */
    public List<Party> getNationalResults(String electionId) {
        return electionService.getElectionData(electionId).getNationalResults();
    }

    /**
     * Calculates seat distribution in an election using the D'Hondt method.
     * This is a pure calculation method.
     *
     * @param results    A list of {@code NationalResult} objects.
     * @param totalSeats The total number of seats to be allocated.
     * @return A {@code Map<String, Integer>} mapping party names to allocated seats.
     */
    public Map<String, Integer> calculateSeats(List<Party> results, int totalSeats) {
        Map<String, Integer> seats = new HashMap<>();
        Map<String, Integer> voteCounts = results.stream()
                .collect(Collectors.toMap(Party::getName, Party::getVotes, Integer::sum));


        for (String party : voteCounts.keySet()) {
            seats.put(party, 0); // initialize seats
        }

        for (int i = 0; i < totalSeats; i++) {
            String maxParty = null;
            double maxValue = -1;
            for (String party : voteCounts.keySet()) {
                int votes = voteCounts.get(party);
                int allocatedSeats = seats.get(party);
                double value = votes / (allocatedSeats + 1.0); // D’Hondt formula
                if (value > maxValue) {
                    maxValue = value;
                    maxParty = party;
                }
            }
            if (maxParty != null) {
                seats.put(maxParty, seats.get(maxParty) + 1);
            }
        }
        return seats;
    }
}