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

            for (nl.hva.elections.xml.model.Candidate xmlCandidate : electionData.getCandidates()) {

                String partyName = xmlCandidate.getPartyName();

                // --- ADD SAFETY CHECK TO SKIP BAD CANDIDATES ---
                if (partyName == null || partyName.isBlank()) {
                    // This prints a message only if the party name is missing
                    System.err.println("Skipping candidate " + xmlCandidate.getId() + " because party name is missing or empty.");
                    continue;
                }

                // Find the JPA Party entity by name using PartyRepository
                partyRepository.findByName(partyName).ifPresentOrElse(jpaParty -> {

                    // Create the new JPA Candidate entity
                    Candidate newCandidateEntity = new Candidate();
                    newCandidateEntity.setName(xmlCandidate.getFirstName() + " " + xmlCandidate.getLastName());
                    newCandidateEntity.setResidence(xmlCandidate.getLocality());
                    newCandidateEntity.setGender(xmlCandidate.getGender());
                    newCandidateEntity.setParty(jpaParty);

                    // Save the new candidate
                    candidateRepository.save(newCandidateEntity);
                }, () -> {
                    // PRINT ERROR IF PARTY NOT FOUND IN DB
                    System.err.println("Lookup failed! No Party found in DB with name: [" + partyName + "]");
                });
            }
            System.out.println("Finished loading candidate data. Total candidates saved: " + candidateRepository.count());
        }
    }
}