package persistence.interfaces;

import domain.StockPriceHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockPriceHistoryDAO {
  StockPriceHistory create(StockPriceHistory history);
  Optional<StockPriceHistory> getById(UUID id);
  List<StockPriceHistory> getAll();
  List<StockPriceHistory> getBySymbol(String symbol);
}
