package presentation.views;

import business.dto.StockDTO;
import business.dto.StockTransactionRequest;
import business.stockmarket.services.StockService;
import business.stockmarket.services.StockTradingService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import presentation.core.NotificationManager;

import java.util.UUID;

import static presentation.core.NotificationManager.NotificationType.*;

public class StockMarketViewModel
{
  private final StockService stockService;
  private final StockTradingService tradingService;
  private final NotificationManager notificationManager;

  private final ObservableList<StockDTO> availableStocks = FXCollections.observableArrayList();
  private final ObjectProperty<StockDTO> selectedStock = new SimpleObjectProperty<>();

  private final XYChart.Series<Number, Number> stockPriceSeries = new XYChart.Series<>();
  private int updateTickCount = 0;

  public StockMarketViewModel(StockService stockService,
      StockTradingService tradingService,
      NotificationManager notificationManager)
  {
    this.stockService = stockService;
    this.tradingService = tradingService;
    this.notificationManager = notificationManager;
  }

  public void updateChartData()
  {
    StockDTO stock = selectedStock.get();
    if (stock == null)
      return;

    stockPriceSeries.setName(stock.symbol() + "Price Timeline");

    Platform.runLater(() -> {
      updateTickCount++;

      stockPriceSeries.getData()
          .add(new XYChart.Data<>(updateTickCount, stock.currentPrice()));

      if (stockPriceSeries.getData().size() > 100)
      {
        stockPriceSeries.getData().remove(0);
      }
    });
  }

  public void loadMarketData()
  {
    try
    {
      availableStocks.setAll(stockService.getAll());
      updateChartData();
    }
    catch (Exception e)
    {
      notificationManager.show("Failed to load market data: " + e.getMessage(),
          ERROR);
    }
  }

  public void selectNewStock(StockDTO newStock)
  {
    if (newStock == null)
      return;

    StockDTO current = selectedStock.get();
    if (current == null || !current.symbol().equals(newStock.symbol()))
    {
      stockPriceSeries.getData().clear();
      updateTickCount = 0;
    }
    selectedStock.set(newStock);
  }

  public void buySelectedStock(UUID currentPortfolioId, int quantity)
  {
    StockDTO stock = selectedStock.get();
    if (stock == null)
    {
      notificationManager.show("Please select a stock from the table first.",
          ERROR);
      return;
    }

    try
    {
      double executionPrice = stock.currentPrice();

      StockTransactionRequest request = new StockTransactionRequest(
          currentPortfolioId, stock.symbol(), quantity);

      tradingService.buy(request);

      String summary = String.format(
          "Receipt Summary:\n" + "• Asset: %s\n" + "• Quantity: %d share\n"
              + "• Execution Price: $%,.2f\n" + "• Total Market Cost: $%,.2f\n"
              + "• Note: Final balance includes fee.",
          stock.symbol(), quantity, executionPrice,
          (executionPrice * quantity));

      notificationManager.show(summary, INFO);
      loadMarketData();
    }
    catch (Exception e)
    {
      notificationManager.show("Purchase failed: " + e.getMessage(), ERROR);
    }
  }

  public void sellSelectedStock(UUID currentPortfolioId, int quantity)
  {
    StockDTO stock = selectedStock.get();

    if (stock == null)
    {
      notificationManager.show("Please select a stock from the table first.",
          ERROR);
      return;
    }

    try
    {
      StockTransactionRequest request = new StockTransactionRequest(
          currentPortfolioId, stock.symbol(), quantity);

      tradingService.sell(request);
      notificationManager.show(
          "Successfully sold " + quantity + " shares of " + stock.symbol(),
          INFO);

      loadMarketData();
    }
    catch (Exception e)
    {
      notificationManager.show("Sale failed: " + e.getMessage(), ERROR);
    }
  }

  public ObservableList<StockDTO> getAvailableStocks()
  {
    return availableStocks;
  }

  public ObjectProperty<StockDTO> selectedStockProperty()
  {
    return selectedStock;
  }

  public XYChart.Series<Number, Number> getStockPriceSeries()
  {
    return stockPriceSeries;
  }
}