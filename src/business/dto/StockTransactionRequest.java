package business.dto;

import java.util.UUID;

public record StockTransactionRequest(UUID portfolioId, String symbol, int quantity)
{
}
