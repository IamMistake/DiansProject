package mk.das.finki.designandarchitectureproject.service.implementation;

import mk.das.finki.designandarchitectureproject.repository.NewsRepository;
import mk.das.finki.designandarchitectureproject.service.DashboardService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Override
    public Map<String, String> getNews(String companySelected) {
        Map<String, String> result = new HashMap<>();

        String companiesLink = NewsRepository.getCompanyUrl(companySelected);

        Map<String, String> newsLinks = NewsRepository.scrapeNewsLinks(companiesLink);
        for (String urlce : newsLinks.keySet()) {
            String news = NewsRepository.getNews(newsLinks.get(urlce));
            String translated = NewsRepository.translateToEnglish(news);
            String score = SentimentDetector.analyzeSentiment(translated);
            System.out.printf("%s -> Url: %s, %s, %s, %s\n", companiesLink, newsLinks.get(urlce), news, translated, score);
            result.putIfAbsent(NewsRepository.translateToEnglish(news.split("\n")[0]), score);
        }

        return result;
    }
}
