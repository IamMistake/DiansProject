package mk.das.finki.designandarchitectureproject.web.controller;

import mk.das.finki.designandarchitectureproject.model.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "http://localhost:63342") // Adjust frontend URL
public class StockController {

    public int getYear(String date){
        return Integer.parseInt(date.split("\\.")[2]);
    }

    @GetMapping("/companies")
    public List<String> getCompanies() {
        String url = "https://www.mse.mk/mk/stats/symbolhistory/kmb";
        return DropdownExtractor.extractDropdownOptions(url);
    }

    @GetMapping("/dashboard/stats")
    public Map<String, Object> getStats(@RequestParam(defaultValue = "ALK") String companySelected,
                                 @RequestParam(defaultValue = "2014") Integer fromYear,
                                 @RequestParam(defaultValue = "2024") Integer toYear) {
        try {
            List<StockData> stockData = CSVProcessor.loadCSV(".\\src\\main\\java\\mk\\das\\finki\\designandarchitectureproject\\bootstrap\\" + companySelected + ".csv");

            Map<String, Object> response = new HashMap<>();

            response.put("data", stockData.get(0));

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    @GetMapping("/dashboard/lineChart")
    public Map<String, Object> getCompanyData(@RequestParam(defaultValue = "ALK") String companySelected,
                                              @RequestParam(defaultValue = "2014") Integer fromYear,
                                              @RequestParam(defaultValue = "2024") Integer toYear) {
        try {
            List<StockData> stockData = CSVProcessor.loadCSV(".\\src\\main\\java\\mk\\das\\finki\\designandarchitectureproject\\bootstrap\\" + companySelected + ".csv");
            List<Double> prices = stockData.stream().filter(sd -> getYear(sd.date) >= fromYear && getYear(sd.date) <= toYear).map(d -> d.closePrice).toList();
            List<Double> sma10 = StockAnalyzer.calculateSMA(prices, 10);
            List<Double> rsi14 = StockAnalyzer.calculateRSI(prices, 14);

            List<String> signals = new ArrayList<>();
            for (int i = 0; i < prices.size(); i++) {
                double sma = sma10.get(i);
                double rsi = rsi14.get(i);
                signals.add(StockAnalyzer.generateSignal(rsi, sma, prices.get(i)));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("dates", stockData.stream().filter(sd -> getYear(sd.date) >= fromYear && getYear(sd.date) <= toYear).map(d -> d.date).toList());
            response.put("prices", prices);
            response.put("sma", sma10);
            response.put("rsi", rsi14);
            response.put("signals", signals);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    @GetMapping("/dashboard/statistics")
    public Map<String, Object> getStatsData(@RequestParam(defaultValue = "ALK") String companySelected,
                                              @RequestParam(defaultValue = "2014") Integer fromYear,
                                              @RequestParam(defaultValue = "2024") Integer toYear) {
        try {
            List<StockData> stockData = CSVProcessor.loadCSV(".\\src\\main\\java\\mk\\das\\finki\\designandarchitectureproject\\bootstrap\\" + companySelected + ".csv");
            List<StockData> filteredData = stockData.stream()
                    .filter(sd -> getYear(sd.date) >= fromYear && getYear(sd.date) <= toYear)
                    .filter(sd -> sd.high != null)
                    .collect(Collectors.toList());

            // Get prices
            List<Double> prices = filteredData.stream().map(d -> d.closePrice).toList();

            // Calculate moving averages
            List<Double> sma10 = StockAnalyzer.calculateSMA(prices, 10);
            List<Double> sma20 = StockAnalyzer.calculateSMA(prices, 20);
            List<Double> ema10 = StockAnalyzer.calculateEMA(prices, 10);
            List<Double> ema20 = StockAnalyzer.calculateEMA(prices, 20);
            List<Double> wma = StockAnalyzer.calculateWMA(prices, 14);

            // Calculate oscillators
            List<Double> rsi = StockAnalyzer.calculateRSI(prices, 14);
            List<Double> stochastic = StockAnalyzer.calculateStochasticOscillator(prices);
            List<Double> macd = StockAnalyzer.calculateMACD(prices);
            List<Double> adx = StockAnalyzer.calculateADX(filteredData);
            List<Double> cci = StockAnalyzer.calculateCCI(filteredData);

            // Generate signals
            List<String> signals = new ArrayList<>();
            for (int i = 0; i < prices.size(); i++) {
                signals.add(StockAnalyzer.generateSignal(rsi.get(i), sma10.get(i), prices.get(i)));
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

    @GetMapping("/dashboard/news")
    public Map<String, String> getNews(@RequestParam(defaultValue = "ALK") String companySelected) {
        Map<String, String> result = new HashMap<>();

        String companiesLink = MseNewsScraper.getCompanyUrl(companySelected);

        Map<String, String> newsLinks = MseNewsScraper.scrapeNewsLinks(companiesLink);
        for (String urlce : newsLinks.keySet()) {
            String news = MseNewsScraper.getNews(newsLinks.get(urlce));
            String translated = MseNewsScraper.translateToEnglish(news);
            String score = SentimentDetector.analyzeSentiment(translated);
            System.out.printf("%s -> Url: %s, %s, %s, %s\n", companiesLink, newsLinks.get(urlce), news, translated, score);
            result.putIfAbsent(MseNewsScraper.translateToEnglish(news.split("\n")[0]), score);
        }

        return result;
    }

}


