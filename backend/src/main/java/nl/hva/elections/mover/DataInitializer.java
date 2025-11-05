package nl.hva.elections.mover;

import nl.hva.elections.persistence.model.Candidate;
import nl.hva.elections.persistence.model.Party;

import nl.hva.elections.repositories.CandidateRepository;
import nl.hva.elections.repositories.PartyRepository;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.PoliticalParty;
import nl.hva.elections.xml.service.DutchElectionService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DutchElectionService xmlService;
    private final PartyRepository partyRepository;
    private final CandidateRepository candidateRepository;

    // Constructor is updated to include CandidateRepository
    public DataInitializer(DutchElectionService xmlService, PartyRepository partyRepository, CandidateRepository candidateRepository) {
        this.xmlService = xmlService;
        this.partyRepository = partyRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Check if both tables have data. If yes, skip.
        if (partyRepository.count() > 0 && candidateRepository.count() > 0) {
            System.out.println("Database already contains party and candidate data. Skipping data load.");
            return;
        }
        System.out.println("Database is empty. Loading data from XML...");


        // Get all data from the XML parser
        Election electionData = xmlService.loadAllElectionData();

        // 1. Save Parties (MUST run first, candidates need the Party IDs)
        if (partyRepository.count() == 0) {
            System.out.println("Loading party data from XML...");
            for (PoliticalParty xmlParty : electionData.getPoliticalParties()) {
                Party newPartyEntity = new Party(
                        xmlParty.getRegisteredAppellation(),
                        null,
                        0
                );
                partyRepository.save(newPartyEntity);
            }
            System.out.println("Finished loading party data. Total parties saved: " + partyRepository.count());
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
                partyRepository.findByName(cleanedPartyName).ifPresentOrElse(jpaParty -> {

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