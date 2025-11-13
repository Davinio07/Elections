package nl.hva.elections.xml.service;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.PoliticalParty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Een service die specifiek verantwoordelijk is voor alle businesslogica
 * rondom Politieke Partijen.
 *
 * Deze service is afhankelijk van de DutchElectionService om de
 * data uit de cache op te halen.
 */
@Service
public class PartyService {

    private final DutchElectionService dutchElectionService;

    // We injecteren de hoofd-service om toegang te krijgen tot de cache
    public PartyService(DutchElectionService dutchElectionService) {
        this.dutchElectionService = dutchElectionService;
    }

    /**
     * Haalt alle politieke partijen op voor een specifieke verkiezing.
     * @param electionId De ID van de verkiezing (bv. "TK2023")
     * @return Een lijst van PoliticalParty objecten
     */
    public List<PoliticalParty> getPoliticalParties(String electionId) {
        // 1. Haal de volledige data op uit de cache via de hoofd-service
        Election election = dutchElectionService.getElectionData(electionId);

        // 2. Pas de businesslogica toe (in dit geval, simpelweg de lijst retourneren)
        return election.getPoliticalParties();

        // Toekomstige logica (bv. filteren, sorteren) zou hier worden toegevoegd.
    }

    // Je zou hier meer partij-specifieke methoden kunnen toevoegen,
    // bijvoorbeeld getPartyById(String electionId, String partyId)
}