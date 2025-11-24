// Bestand: elections/admin/AdminService.java
package nl.hva.elections.admin;

import nl.hva.elections.repositories.CandidateRepository;
import nl.hva.elections.repositories.PartyRepository;
import nl.hva.elections.repositories.UserRepository;
import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.Party;
import nl.hva.elections.xml.service.DutchElectionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class AdminService {

    private final PartyRepository partyRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final DutchElectionService electionService;

    // Constructor Injection voor alle benodigde data-bronnen
    public AdminService(PartyRepository partyRepository, 
                        CandidateRepository candidateRepository,
                        UserRepository userRepository,
                        DutchElectionService electionService) {
        this.partyRepository = partyRepository;
        this.candidateRepository = candidateRepository;
        this.userRepository = userRepository;
        this.electionService = electionService;
    }

    public List<Map<String, Object>> getDashboardStats() {
        // 1. Haal totaal aantal stemmen uit de XML cache (sneller dan DB sum)
        long totalVotes = 0;
        try {
            Election election = electionService.loadAllElectionData();
            totalVotes = election.getNationalResults().stream()
                    .mapToLong(Party::getVotes)
                    .sum();
        } catch (Exception e) {
            totalVotes = 0; // Fallback
        }

        // 2. Tel entiteiten in de database
        long totalParties = partyRepository.count();
        long totalCandidates = candidateRepository.count();
        long totalUsers = userRepository.count();

        // Formatter voor nette getallen (bijv. 1.000.000)
        NumberFormat nf = NumberFormat.getInstance(new Locale("nl", "NL"));

        return List.of(
                Map.of("id", 1, "title", "Totaal Stemmen (TK2023)", "value", nf.format(totalVotes), "icon", "🗳️"),
                Map.of("id", 2, "title", "Geregistreerde Partijen", "value", String.valueOf(totalParties), "icon", "📢"),
                Map.of("id", 3, "title", "Aantal Kandidaten", "value", String.valueOf(totalCandidates), "icon", "👥"),
                Map.of("id", 4, "title", "Geregistreerde Gebruikers", "value", String.valueOf(totalUsers), "icon", "👤")
        );
    }
}