package business.stockmarket.services;

import domain.OwnedStock;
import persistence.interfaces.OwnedStockDAO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MockOwnedStockDAO implements OwnedStockDAO {
  private final MockUnitOfWork uow;

  public MockOwnedStockDAO(MockUnitOfWork uow) {
    this.uow = uow;
  }

  @Override
  public OwnedStock create(OwnedStock os) {
    uow.getOwnedStocks().add(os);
    return os;
  }

  @Override
  public void update(OwnedStock os) {
  }

  @Override
  public boolean delete(UUID id) {
    return uow.getOwnedStocks().removeIf(os -> os.getId().equals(id));
  }

  @Override
  public Optional<OwnedStock> getById(UUID id) {
    return uow.getOwnedStocks().stream().filter(os -> os.getId().equals(id)).findFirst();
  }

  @Override
  public Optional<OwnedStock> getByPortfolioIdAndStockSymbol(UUID portfolioId, String stockSymbol) {
    return uow.getOwnedStocks().stream()
        .filter(os -> os.getPortfolioId().equals(portfolioId) && os.getStockSymbol().equals(stockSymbol))
        .findFirst();
  }

  @Override
  public List<OwnedStock> getAll() {
    return uow.getOwnedStocks();
  }
}