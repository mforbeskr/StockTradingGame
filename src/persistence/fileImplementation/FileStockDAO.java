package persistence.fileImplementation;

import domain.Stock;
import persistence.interfaces.StockDAO;
import shared.logging.Logger;

import java.util.List;
import java.util.Optional;

public class FileStockDAO implements StockDAO
{
  private final FileUnitOFWork uow;


  public FileStockDAO(FileUnitOFWork uow)
  {
    this.uow = uow;
  }

  @Override public Stock create(Stock stock)
  { // prevent duplicates, since Stocks identify by 'string symbols'
    if (getBySymbol(stock.getSymbol()).isPresent())
    {
      throw new RuntimeException(
          "Stock with symbol " + stock.getSymbol() + " already exists!");
    }
    uow.getStocks().add(stock);
    return stock;
  }

  @Override public void update(Stock stock)
  {
    List<Stock> list = uow.getStocks();
    for (int i = 0; i < list.size(); i++)
    {
      if (list.get(i).getSymbol().equals(stock.getSymbol()))
      {
        list.set(i, stock);
        System.out.println("Stock successfully updated!");
        return;
      }
    }
    // When/if the stock isn't found
    Logger.getInstance().log("ERROR",
        "Update FAILED: Stock '" + stock.getSymbol() + "' not found.");
    throw new RuntimeException(
        "Critical Update Error: Stock '" + stock.getSymbol() + "' not found.");
  }

  @Override public boolean delete(String symbol)
  {
    List<Stock> stocks = uow.getStocks();
    boolean removed = stocks.removeIf(s -> s.getSymbol().equals(symbol));

    if (!removed)
    {
      // log warning - tried to delete a 'ghost' stock
      Logger.getInstance()
          .log("ERROR", "Delete FAILED: Stock '" + symbol + "' not found.");
    }
    return removed;
  }

  @Override public List<Stock> getAll()
  {
    return List.copyOf(uow.getStocks());
  }

  @Override public Optional<Stock> getBySymbol(String symbol)
  {
    for (Stock stock : uow.getStocks())
    {
      if (stock.getSymbol().equals(symbol))
      {
        return Optional.of(stock);
      }
    }
    return Optional.empty();
  }
}
