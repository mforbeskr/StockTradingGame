package business.dto;

public record OwnedStockOverviewDTO(String symbol, int sharesOwned, double currentPrice,
                                    double totalValue)

{
}
