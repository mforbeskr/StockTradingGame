package persistence.interfaces;

import domain.Stock;

import java.util.List;
import java.util.Optional;

public interface StockDAO
{
  Stock create(Stock stock);
  void update(Stock stock);
  boolean delete(String symbol);
  List<Stock> getAll();
  Optional<Stock> getBySymbol(String symbol); // symbols identify stocks
}
