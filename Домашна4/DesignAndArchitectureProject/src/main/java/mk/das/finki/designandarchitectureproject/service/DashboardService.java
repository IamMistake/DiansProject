package mk.das.finki.designandarchitectureproject.service;

import mk.das.finki.designandarchitectureproject.model.StockData;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    // Fetches statistical data from the given CSV file URL
    Map<String, Object> getStats(String url);

    // Retrieves company-specific data for a given time range from the CSV file URL
    Map<String, Object> getCompanyData(String url, Integer fromYear, Integer toYear);

    // Fetches detailed statistical data for a given time range from the CSV file URL
    Map<String, Object> getStatsData(String url, Integer fromYear, Integer toYear);

    // Retrieves news articles or updates related to the specified company
    Map<String, String> getNews(String companySelected);
}

