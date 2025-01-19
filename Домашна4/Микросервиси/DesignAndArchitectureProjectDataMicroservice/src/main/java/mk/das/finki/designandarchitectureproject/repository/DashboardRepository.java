package mk.das.finki.designandarchitectureproject.repository;

import mk.das.finki.designandarchitectureproject.model.CSVProcessor;
import mk.das.finki.designandarchitectureproject.model.StockData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardRepository {
    public List<StockData> loadCSV(String url) throws Exception {
        return CSVProcessor.loadCSV(url);
    }
}
