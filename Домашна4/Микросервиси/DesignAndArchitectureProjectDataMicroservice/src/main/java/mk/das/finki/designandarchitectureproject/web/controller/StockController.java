package mk.das.finki.designandarchitectureproject.web.controller;

import mk.das.finki.designandarchitectureproject.model.*;
import mk.das.finki.designandarchitectureproject.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:63342") // Adjust frontend URL
public class StockController {

    public final DashboardService dashboardService;

    public StockController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/stats")
    public Map<String, Object> getStats(@RequestParam(defaultValue = "ALK") String companySelected,
                                    @RequestParam(defaultValue = "2014") Integer fromYear,
                                    @RequestParam(defaultValue = "2024") Integer toYear) {
        return dashboardService.getStats(".\\src\\main\\java\\mk\\das\\finki\\designandarchitectureproject\\bootstrap\\" + companySelected + ".csv");
    }

    @GetMapping("/dashboard/lineChart")
    public Map<String, Object> getCompanyData(@RequestParam(defaultValue = "ALK") String companySelected,
                                              @RequestParam(defaultValue = "2014") Integer fromYear,
                                              @RequestParam(defaultValue = "2024") Integer toYear) {
        return dashboardService.getCompanyData(".\\src\\main\\java\\mk\\das\\finki\\designandarchitectureproject\\bootstrap\\" + companySelected + ".csv", fromYear, toYear);
    }

    @GetMapping("/dashboard/statistics")
    public Map<String, Object> getStatsData(@RequestParam(defaultValue = "ALK") String companySelected,
                                              @RequestParam(defaultValue = "2014") Integer fromYear,
                                              @RequestParam(defaultValue = "2024") Integer toYear) {
        return dashboardService.getStatsData(".\\src\\main\\java\\mk\\das\\finki\\designandarchitectureproject\\bootstrap\\" + companySelected + ".csv", fromYear, toYear);
    }

}


