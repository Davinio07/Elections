package nl.hva.elections.OnStartUp;

import nl.hva.elections.repositories.ProvinceRepository;
import nl.hva.elections.persistence.model.Province;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order; // <--- Import this
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1) // <--- ADD THIS: Forces this to run before DataInitializer
public class DataSeeder implements ApplicationRunner {

    private final ProvinceRepository provinceRepository;

    @Autowired
    public DataSeeder(ProvinceRepository provinceRepository) {
        this.provinceRepository = provinceRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (provinceRepository.count() == 0) {
            System.out.println("Database is empty. Seeding Provinces...");

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

            provinceRepository.saveAll(provinces);
            System.out.println(provinces.size() + " provinces have been saved.");
        }
    }
}