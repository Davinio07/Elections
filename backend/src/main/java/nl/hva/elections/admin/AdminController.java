package nl.hva.elections.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired // Injecteer de AdminService
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("message", "Pong! De connectie met de Java backend werkt! 🐧");
    }

    @GetMapping("/stats")
    public List<Map<String, Object>> getStats() {
        // Delegeer de logica naar de service
        return adminService.getDashboardStats();
    }
}