package mk.das.finki.designandarchitectureproject.web.controller;

import mk.das.finki.designandarchitectureproject.model.*;
import mk.das.finki.designandarchitectureproject.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:63342") // Adjust frontend URL
public class StockNewsController {

    public final DashboardService dashboardService;

    public StockNewsController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/companies")
    public List<String> getCompanies() {
        String url = "https://www.mse.mk/mk/stats/symbolhistory/kmb";
        return Utils.extractDropdownOptions(url);
    }

    @GetMapping("/dashboard/news")
    public Map<String, String> getNews(@RequestParam(defaultValue = "ALK") String companySelected) {
        return dashboardService.getNews(companySelected);
    }

}


