package nl.hva.elections.mover;

import nl.hva.elections.persistence.model.Party;

import nl.hva.elections.repositories.PartyRepository;

import nl.hva.elections.xml.model.Election;
import nl.hva.elections.xml.model.PoliticalParty;
import nl.hva.elections.xml.service.DutchElectionService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner { // Tells Spring: "Run this code once the app starts."

    private final DutchElectionService xmlService;
    private final PartyRepository partyRepository;

    public DataInitializer(DutchElectionService xmlService, PartyRepository partyRepository) {
        this.xmlService = xmlService;
        this.partyRepository = partyRepository;
    }

    // This 'run' method is the code that Spring will execute at startup.
    @Override
    public void run(String... args) throws Exception {

        // check if we've already loaded the data. No need to do it twice.
        if (partyRepository.count() > 0) {
            System.out.println("Database already contains party data. Skipping data load.");
            return;
        }
        System.out.println("Database is empty. Loading party data from XML...");

        // Get all data from the XML parser (old service)
        Election electionData = xmlService.loadAllElectionData();

        // Loop through just the parties from that XML data
        for (PoliticalParty xmlParty : electionData.getPoliticalParties()) {

            // We use the constructor you built in your Party entity
            Party newPartyEntity = new Party(
                    xmlParty.getRegisteredAppellation(), // The name
                    null, // TODO: Add the logo URL if you have it
                    0     // TODO: Add national seats if you have it
            );

            // Note: If 'PoliticalParty' class has logo/seats, use its getters here!
            // Example:
            // newPartyEntity.setLogoUrl(xmlParty.getLogoUrl());
            // newPartyEntity.setNationalSeats(xmlParty.getNationalSeats());

            // D. Save the new, translated party into the H2 Database
            partyRepository.save(newPartyEntity);
        }
        System.out.println("Finished loading party data. Total parties saved: " + partyRepository.count());
    }
}