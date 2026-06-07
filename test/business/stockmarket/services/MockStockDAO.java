package business.stockmarket.services;

import domain.Stock;
import persistence.interfaces.StockDAO;
import java.util.List;
import java.util.Optional;

public class MockStockDAO implements StockDAO {
  private final MockUnitOfWork uow;

  public MockStockDAO(MockUnitOfWork uow) {
    this.uow = uow;
  }

  @Override
  public Stock create(Stock s) {
    uow.getStocks().add(s);
    return s;
  }

  @Override
  public void update(Stock s) {
  }

  @Override
  public boolean delete(String symbol) {
    return uow.getStocks().removeIf(st -> st.getSymbol().equals(symbol));
  }

  @Override
  public List<Stock> getAll() {
    return uow.getStocks();
  }

  @Override
  public Optional<Stock> getBySymbol(String symbol) {
    return uow.getStocks().stream().filter(st -> st.getSymbol().equals(symbol)).findFirst();
  }
}