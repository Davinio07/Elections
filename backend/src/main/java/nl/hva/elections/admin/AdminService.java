package nl.hva.elections.admin;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service // Markeer dit als een Spring Service component
public class AdminService {

    /**
     * Haalt een lijst met dashboardstatistieken op.
     * In een echte applicatie zou deze data uit een database komen.
     * @return Een lijst met statistieken.
     */
    public List<Map<String, Object>> getDashboardStats() {
        return List.of(
                Map.of("id", 1, "title", "Totaal Aantal Stemmen", "value", "1,234,567", "icon", "🗳️"),
                Map.of("id", 2, "title", "Actieve Verkiezingen", "value", "5", "icon", "📢"),
                Map.of("id", 3, "title", "Geregistreerde Gebruikers", "value", "4,321", "icon", "👥"),
                Map.of("id", 4, "title", "Opkomstpercentage", "value", "78.9%", "icon", "📈")
        );
    }
}