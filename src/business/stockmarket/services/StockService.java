package business.stockmarket.services;

import business.dto.StockDTO;
import domain.Stock;
import persistence.interfaces.StockDAO;

import java.util.ArrayList;
import java.util.List;

public class StockService
{
  private final StockDAO stockDAO;

  public StockService(StockDAO stockDAO)
  {
    this.stockDAO = stockDAO;
  }

  private StockDTO mapStockToDTO(Stock stock)
  {
    return new StockDTO(stock.getSymbol(), stock.getName(),
        stock.getCurrentPrice(), stock.getCurrentState());
  }

  private List<StockDTO> mapStockListToDTO(List<Stock> stocks)
  {
    List<StockDTO> result = new ArrayList<>();

    for (Stock stock : stocks)
    {
      StockDTO dto = mapStockToDTO(stock);
      result.add(dto);
    }
    return result;
  }

  public List<StockDTO> getAll()
  {
    return mapStockListToDTO(stockDAO.getAll());
  }
}
