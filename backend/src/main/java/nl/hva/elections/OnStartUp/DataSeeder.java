package nl.hva.elections.OnStartUp;

import nl.hva.elections.repositories.ProvinceRepository;
import nl.hva.elections.xml.model.Province;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class runs once when the Spring Boot application starts.
 * It's used to "seed" the database with initial, static data,
 * like the 12 provinces of the Netherlands.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    // Spring will automatically connect this to your ProvinceRepository
    private final ProvinceRepository provinceRepository;

    @Autowired
    public DataSeeder(ProvinceRepository provinceRepository) {
        this.provinceRepository = provinceRepository;
    }

    /**
     * This method is automatically called on startup.
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Check if the province table is already populated
        if (provinceRepository.count() == 0) {
            System.out.println("Database is empty. Seeding Provinces...");

            // Create the list of 12 provinces
            List<Province> provinces = List.of(
                    new Province(1, "Groningen"),
                    new Province(2, "Friesland"),
                    new Province(3, "Drenthe"),
                    new Province(4, "Overijssel"),
                    new Province(5, "Flevoland"),
                    new Province(6, "Gelderland"),
                    new Province(7, "Utrecht"),
                    new Province(8, "Noord-Holland"),
                    new Province(9, "Zuid-Holland"),
                    new Province(10, "Zeeland"),
                    new Province(11, "Noord-Brabant"),
                    new Province(12, "Limburg")
            );

            // Save them all to the database
            provinceRepository.saveAll(provinces);
            System.out.println(provinces.size() + " provinces have been saved.");
        } else {
            System.out.println("Database already contains province data. Skipping seed.");
        }
    }
}