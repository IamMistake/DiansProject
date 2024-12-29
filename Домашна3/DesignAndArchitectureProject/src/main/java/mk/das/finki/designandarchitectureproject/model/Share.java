package mk.das.finki.designandarchitectureproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Share {
    //Date,Price of last transaction,Max.,Min.,Average price,%prom.,Quantity,BEST turnover in denars,Total turnover in denars
    private LocalDateTime date;
    private long priceOfLastTransaction;
    private long maxPrice;
    private long minPrice;
    private long averagePrice;
    private long prom;
    private long quantity;
    private long BESTturnoverInDenars;
    private long totalTurnoverInDenars;
}
