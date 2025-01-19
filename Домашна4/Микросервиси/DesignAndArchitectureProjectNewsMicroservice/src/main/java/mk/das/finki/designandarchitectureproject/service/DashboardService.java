package mk.das.finki.designandarchitectureproject.service;

import java.util.Map;

public interface DashboardService {
    Map<String, String> getNews(String companySelected);
}
