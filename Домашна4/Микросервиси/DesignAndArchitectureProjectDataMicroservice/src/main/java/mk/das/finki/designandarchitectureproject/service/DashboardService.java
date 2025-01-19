package mk.das.finki.designandarchitectureproject.service;

import mk.das.finki.designandarchitectureproject.model.StockData;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getStats(String url);
    Map<String, Object> getCompanyData(String url, Integer fromYear, Integer toYear);
    Map<String, Object> getStatsData(String url, Integer fromYear, Integer toYear);
}
