package domain;

import java.time.Instant;
import java.util.UUID;

public record StockPriceHistory(double price, String stockSymbol, UUID id,
                                Instant timestamp)
{
}
