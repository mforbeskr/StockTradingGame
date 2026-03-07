package persistence.interfaces;

import domain.StockPriceHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockPriceHistoryDAO {
  StockPriceHistory create(StockPriceHistory history); // I chose to "leave it, just in case." but here i will make it append instead
  Optional<StockPriceHistory> getById(UUID id);
  List<StockPriceHistory> getAll();
  List<StockPriceHistory> getBySymbol(String symbol);
  void append(StockPriceHistory stockPriceHistory);

}
