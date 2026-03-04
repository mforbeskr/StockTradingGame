package persistence.fileImplementation;

import domain.StockPriceHistory;
import persistence.interfaces.StockPriceHistoryDAO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileStockPriceHistoryDAO implements StockPriceHistoryDAO {
  private final FileUnitOFWork uow;

  public FileStockPriceHistoryDAO(FileUnitOFWork uow) {
    this.uow = uow;
  }

  @Override
  public StockPriceHistory create(StockPriceHistory history) {
    uow.getStockPriceHistoryBuffer().add(history);
    return history;
  }

  @Override
  public List<StockPriceHistory> getAll() {
    return List.copyOf(uow.getStockPriceHistories());
  }

  @Override
  public Optional<StockPriceHistory> getById(UUID id) {
    return uow.getStockPriceHistories().stream()
        .filter(h -> h.getId().equals(id))
        .findFirst();
  }

  // Helper method specific to history
  public List<StockPriceHistory> getBySymbol(String symbol) {
    return uow.getStockPriceHistories().stream()
        .filter(h -> h.getStockSymbol().equals(symbol))
        .collect(Collectors.toList());
  }
}