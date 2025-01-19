package mk.das.finki.designandarchitectureproject.web.controller;

import mk.das.finki.designandarchitectureproject.model.*;
import mk.das.finki.designandarchitectureproject.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:63342") // Allow cross-origin requests from the specified frontend URL
public class StockController {

    public final DashboardService dashboardService;

    public StockController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/stats")
    // Endpoint to fetch statistical data for a selected company and time range
    public Map<String, Object> getStats(@RequestParam(defaultValue = "ALK") String companySelected,
                                        @RequestParam(defaultValue = "2014") Integer fromYear,
                                        @RequestParam(defaultValue = "2024") Integer toYear) {
        return dashboardService.getStats("./database/" + companySelected + ".csv");
    }

    @GetMapping("/dashboard/lineChart")
    // Endpoint to fetch line chart data for a selected company and time range
    public Map<String, Object> getCompanyData(@RequestParam(defaultValue = "ALK") String companySelected,
                                              @RequestParam(defaultValue = "2014") Integer fromYear,
                                              @RequestParam(defaultValue = "2024") Integer toYear) {
        return dashboardService.getCompanyData("./database/" + companySelected + ".csv", fromYear, toYear);
    }

    @GetMapping("/dashboard/statistics")
    // Endpoint to fetch detailed statistics for a selected company and time range
    public Map<String, Object> getStatsData(@RequestParam(defaultValue = "ALK") String companySelected,
                                            @RequestParam(defaultValue = "2014") Integer fromYear,
                                            @RequestParam(defaultValue = "2024") Integer toYear) {
        return dashboardService.getStatsData("./database/" + companySelected + ".csv", fromYear, toYear);
    }

    @GetMapping("/companies")
    // Endpoint to retrieve a list of company symbols from a remote URL
    public List<String> getCompanies() {
        String url = "https://www.mse.mk/mk/stats/symbolhistory/kmb";
        return Utils.extractDropdownOptions(url);
    }

    @GetMapping("/dashboard/news")
    // Endpoint to fetch news for a selected company
    public Map<String, String> getNews(@RequestParam(defaultValue = "ALK") String companySelected) {
        return dashboardService.getNews(companySelected);
    }

}
