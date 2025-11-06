package nl.hva.elections.mover;

import nl.hva.elections.persistence.model.Candidate;
import nl.hva.elections.persistence.model.Party;
import nl.hva.elections.repositories.CandidateRepository;
import nl.hva.elections.repositories.PartyRepository;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.NationalResult;
import nl.hva.elections.xml.service.DutchElectionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import java.util.List;
import java.util.Map;

/**
 * Initializes the database with election data from XML files upon application startup.
 * This {@link CommandLineRunner} implementation checks if the database is empty.
 * If it is, it loads, parses, and processes data for a predefined list of elections.
 * <p>
 * This process includes:
 * 1. Parsing the XML data (results, candidates) for each election year.
 * 2. Calculating the seat distribution (using D'Hondt) and vote percentages.
 * 3. Persisting the calculated {@link Party} data to the database.
 * 4. Persisting the {@link Candidate} data, linking it to the correct party and election.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final DutchElectionService xmlService;
    private final PartyRepository partyRepository;
    private final CandidateRepository candidateRepository;

    /**
     * A predefined list of election identifiers (folder names) to load.
     */
    private static final List<String> electionList = List.of("TK2023", "TK2021");

    /**
     * The total number of seats to be allocated in the election (e.g., 150 for TK).
     */
    private static final int totalSeats = 150;

    /**
     * Constructs a new DataInitializer with required service and repositories.
     *
     * @param xmlService          The service for XML data retrieval.
     * @param partyRepository     The repository for party data.
     * @param candidateRepository The repository for candidate data.
     */
    public DataInitializer(DutchElectionService xmlService, PartyRepository partyRepository, CandidateRepository candidateRepository) {
        this.xmlService = xmlService;
        this.partyRepository = partyRepository;
        this.candidateRepository = candidateRepository;
    }

    /**
     * Executes the data initialization logic when the application starts.
     *
     * @param args Command line arguments (not used).
     * @throws Exception if a critical error occurs during data loading, stopping the application.
     */
    @Override
    public void run(String... args) throws Exception {

        // Check if data already exists to prevent re-initialization
        if (partyRepository.count() > 0 || candidateRepository.count() > 0) {
            System.out.println("Database already contains data. Skipping data load.");
            return;
        }

        System.out.println("Database is empty. Loading data from XML for " + electionList.size() + " elections...");
        for (String electionId : electionList) {
            System.out.println("--- Loading data for: " + electionId + " ---");

            // Load all XML data for this specific election
            Election electionData = xmlService.readResults(electionId, electionId);

            // --- 1. Process and Save Parties ---
            List<NationalResult> rawResults = electionData.getNationalResults();
            if (rawResults == null || rawResults.isEmpty()) {
                System.err.println("No national results found for " + electionId + ". Skipping party load.");
            } else {
                // Step 1: Calculate seat distribution (D'Hondt)
                Map<String, Integer> calculatedSeatsMap = xmlService.calculateSeats(rawResults, totalSeats);

                // Step 2: Calculate total votes for percentage calculation
                double totalVotes = rawResults.stream()
                        .mapToDouble(NationalResult::getValidVotes)
                        .sum();

                // Step 3: Iterate, combine calculated data, and save Party entities
                for (NationalResult rawResult : rawResults) {
                    String partyName = rawResult.getPartyName();
                    int votes = rawResult.getValidVotes();

                    // Retrieve calculated values
                    int calculatedSeats = calculatedSeatsMap.getOrDefault(partyName, 0);
                    double calculatedPercentage = (totalVotes > 0) ? ((double) votes / totalVotes) * 100.0 : 0.0;

                    // Create and persist the complete Party entity
                    Party jpaParty = new Party();
                    jpaParty.setElectionId(electionId);
                    jpaParty.setName(partyName);
                    jpaParty.setVotes(votes);
                    jpaParty.setSeats(calculatedSeats);
                    jpaParty.setPercentage(calculatedPercentage);

                    partyRepository.save(jpaParty);
                }
                System.out.println("Saved " + rawResults.size() + " parties for " + electionId + " with calculated seats/percentages.");
            }


            // 2. Save Candidates
            if (candidateRepository.count() == 0) {
                System.out.println("Loading candidate data from XML...");

                final AtomicInteger candidatesSaved = new AtomicInteger(0);

                for (nl.hva.elections.xml.model.Candidate xmlCandidate : electionData.getCandidates()) {

                    String partyName = xmlCandidate.getPartyName();

                    if (partyName == null || partyName.isBlank()) {
                        System.err.println("Skipping candidate " + xmlCandidate.getId() + " because party name is missing or empty.");
                        continue;
                    }

                    String cleanedPartyName = partyName.trim();

                    // --- 1. DETERMINE CANDIDATE'S FULL NAME (as saved in DB) ---
                    String candidateFullName = (xmlCandidate.getFirstName() + " " + xmlCandidate.getLastName()).trim();
                    // -------------------------------------------------------------

                    // Find the JPA Party entity by name using PartyRepository
                    partyRepository.findByNameAndElectionId(cleanedPartyName, electionId).ifPresentOrElse(jpaParty -> {

                        // --- 2. CHECK FOR DUPLICATES BEFORE SAVING ---
                        if (candidateRepository.existsByNameAndPartyId(candidateFullName, jpaParty.getId())) {
                            System.out.println("SKIPPING DUPLICATE: Candidate '" + candidateFullName + "' already exists for party " + jpaParty.getName());
                            return; // Skip to the next iteration
                        }
                        // ---------------------------------------------

                        // 3. Save Candidate (Only if no duplicate found)
                        Candidate newCandidateEntity = new Candidate();
                        newCandidateEntity.setName(candidateFullName); // Use the full name for the final entity
                        newCandidateEntity.setResidence(xmlCandidate.getLocality());
                        newCandidateEntity.setGender(xmlCandidate.getGender());
                        newCandidateEntity.setParty(jpaParty);

                        candidateRepository.save(newCandidateEntity);

                        candidatesSaved.incrementAndGet();

                    }, () -> {
                        System.err.println("Lookup failed! No Party found in DB with name: [" + cleanedPartyName + "]");
                    });
                }
                System.out.println("Finished loading candidate data. Total candidates saved: " + candidatesSaved.get());
            }
        }
    }
}