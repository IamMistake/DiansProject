package mk.das.finki.designandarchitectureproject.model;

public class StockData {
    public String date;
    public Double closePrice;
    public Double high;
    public Double low;
    public Double avgPrice;
    public Double volume;

    public StockData(String date, Double closePrice, Double high, Double low, Double avgPrice, Double volume) {
        this.date = date;
        this.closePrice = closePrice;
        this.high = high;
        this.low = low;
        this.avgPrice = avgPrice;
        this.volume = volume;
    }
}
