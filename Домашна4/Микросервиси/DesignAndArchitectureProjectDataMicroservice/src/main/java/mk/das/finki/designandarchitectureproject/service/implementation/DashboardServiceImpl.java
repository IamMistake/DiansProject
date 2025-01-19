package mk.das.finki.designandarchitectureproject.service.implementation;

import mk.das.finki.designandarchitectureproject.model.StockData;
import mk.das.finki.designandarchitectureproject.model.Utils;
import mk.das.finki.designandarchitectureproject.repository.DashboardRepository;
import mk.das.finki.designandarchitectureproject.repository.StockRepository;
import mk.das.finki.designandarchitectureproject.service.DashboardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardServiceImpl(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public Map<String, Object> getStats(String url) {
        try {
            List<StockData> stockData = dashboardRepository.loadCSV(url);

            Map<String, Object> response = new HashMap<>();

            response.put("data", stockData.get(0));

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    @Override
    public Map<String, Object> getCompanyData(String url, Integer fromYear, Integer toYear) {
        try {
            List<StockData> stockData = dashboardRepository.loadCSV(url);
            List<Double> prices = stockData.stream().filter(sd -> Utils.getYear(sd.date) >= fromYear && Utils.getYear(sd.date) <= toYear).map(d -> d.closePrice).toList();
            List<Double> sma10 = StockRepository.calculateSMA(prices, 10);
            List<Double> rsi14 = StockRepository.calculateRSI(prices, 14);

            List<String> signals = new ArrayList<>();
            for (int i = 0; i < prices.size(); i++) {
                double sma = sma10.get(i);
                double rsi = rsi14.get(i);
                signals.add(StockRepository.generateSignal(rsi, sma, prices.get(i)));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("dates", stockData.stream().filter(sd -> Utils.getYear(sd.date) >= fromYear && Utils.getYear(sd.date) <= toYear).map(d -> d.date).toList());
            response.put("prices", prices);
            response.put("sma", sma10);
            response.put("rsi", rsi14);
            response.put("signals", signals);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    @Override
    public Map<String, Object> getStatsData(String url, Integer fromYear, Integer toYear) {
        try {
            List<StockData> stockData = dashboardRepository.loadCSV(url);
            List<StockData> filteredData = stockData.stream()
                    .filter(sd -> Utils.getYear(sd.date) >= fromYear && Utils.getYear(sd.date) <= toYear)
                    .filter(sd -> sd.high != null)
                    .collect(Collectors.toList());

            // Get prices
            List<Double> prices = filteredData.stream().map(d -> d.closePrice).toList();

            // Calculate moving averages
            List<Double> sma10 = StockRepository.calculateSMA(prices, 10);
            List<Double> sma20 = StockRepository.calculateSMA(prices, 20);
            List<Double> ema10 = StockRepository.calculateEMA(prices, 10);
            List<Double> ema20 = StockRepository.calculateEMA(prices, 20);
            List<Double> wma = StockRepository.calculateWMA(prices, 14);

            // Calculate oscillators
            List<Double> rsi = StockRepository.calculateRSI(prices, 14);
            List<Double> stochastic = StockRepository.calculateStochasticOscillator(prices);
            List<Double> macd = StockRepository.calculateMACD(prices);
            List<Double> adx = StockRepository.calculateADX(filteredData);
            List<Double> cci = StockRepository.calculateCCI(filteredData);

            // Generate signals
            List<String> signals = new ArrayList<>();
            for (int i = 0; i < prices.size(); i++) {
                signals.add(StockRepository.generateSignal(rsi.get(i), sma10.get(i), prices.get(i)));
            }

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("dates", filteredData.stream().map(d -> d.date).toList());
            response.put("prices", prices);
            response.put("sma", Map.of("sma10", sma10, "sma20", sma20, "ema10", ema10, "ema20", ema20, "wma", wma));
            response.put("oscillators", Map.of("rsi", rsi, "stochastic", stochastic, "macd", macd, "adx", adx, "cci", cci));
            response.put("signals", signals);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }
}
