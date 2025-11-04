package nl.hva.elections.mover;

import nl.hva.elections.persistence.model.Candidate; // <-- NEW
import nl.hva.elections.persistence.model.Party;

import nl.hva.elections.repositories.CandidateRepository; // <-- NEW
import nl.hva.elections.repositories.PartyRepository;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.NationalResult;
import nl.hva.elections.xml.model.PoliticalParty;
import nl.hva.elections.xml.service.DutchElectionService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DutchElectionService xmlService;
    private final PartyRepository partyRepository;
    private final CandidateRepository candidateRepository; // <-- NEW: Injected Repository

    private static final List<String> electionList = List.of("TK2023", "TK2021");

    // Constructor is updated to include CandidateRepository
    public DataInitializer(DutchElectionService xmlService, PartyRepository partyRepository, CandidateRepository candidateRepository) {
        this.xmlService = xmlService;
        this.partyRepository = partyRepository;
        this.candidateRepository = candidateRepository; // <-- NEW
    }

    @Override
    public void run(String... args) throws Exception {

        // Check if data already exists. If so, skip initialization.
        if (partyRepository.count() > 0 || candidateRepository.count() > 0) {
            System.out.println("Database already contains data. Skipping data load.");
            return;
        }

        System.out.println("Database is empty. Loading data from XML for " + electionList.size() + " elections...");

        // Loop through each specified election ID
        for (String electionId : electionList) {
            System.out.println("--- Loading data for: " + electionId + " ---");

            // Load the specific election data from XML
            Election electionData = xmlService.readResults(electionId, electionId);

            // 1. Save Parties
            // We use NationalResult as the source, as it contains all necessary data
            // (name, votes, seats, percentage) for the Party entity.
            if (electionData.getNationalResults() != null) {
                for (NationalResult xmlResult : electionData.getNationalResults()) {
                    Party jpaParty = new Party();
                    jpaParty.setElectionId(electionId); // <-- Set the election identifier
                    jpaParty.setName(xmlResult.getPartyName());
                    jpaParty.setVotes(xmlResult.getValidVotes());
                    jpaParty.setSeats(xmlResult.getSeats());
                    jpaParty.setPercentage(xmlResult.getVotePercentage()); // <-- Corrected: was setVotes

                    partyRepository.save(jpaParty);
                }
                System.out.println("Saved " + electionData.getNationalResults().size() + " parties for " + electionId);
            }

            // 2. Save Candidates
            if (electionData.getCandidates() != null) {
                int savedCandidates = 0;
                for (nl.hva.elections.xml.model.Candidate xmlCandidate : electionData.getCandidates()) {
                    String partyName = xmlCandidate.getPartyName();

                    if (partyName == null || partyName.isBlank()) {
                        System.err.println("Skipping candidate " + xmlCandidate.getId() + " because party name is missing.");
                        continue;
                    }

                    // Find the corresponding Party entity using both name and electionId
                    partyRepository.findByNameAndElectionId(partyName, electionId).ifPresentOrElse(jpaParty -> {
                        Candidate newCandidateEntity = new Candidate();
                        newCandidateEntity.setName(xmlCandidate.getFirstName() + " " + xmlCandidate.getLastName());
                        newCandidateEntity.setResidence(xmlCandidate.getLocality());
                        newCandidateEntity.setGender(xmlCandidate.getGender());
                        newCandidateEntity.setParty(jpaParty); // Link to the Party entity

                        candidateRepository.save(newCandidateEntity);
                    }, () -> {
                        System.err.println("Lookup failed for " + electionId + " candidate! No Party found in DB with name: [" + partyName + "]");
                    });
                    savedCandidates++;
                }
                System.out.println("Saved " + savedCandidates + " candidates for " + electionId);
            }
        }
        System.out.println("--- Database initialization complete. ---");
    }
}