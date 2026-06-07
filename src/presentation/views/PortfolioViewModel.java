package presentation.views;

import business.dto.OwnedStockOverviewDTO;
import business.dto.PortfolioOverviewDTO;
import business.dto.StockTransactionRequest;
import business.stockmarket.services.GameService;
import business.stockmarket.services.PortfolioService;
import business.stockmarket.services.StockTradingService;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import presentation.core.NotificationManager;

import java.util.UUID;

import static presentation.core.NotificationManager.NotificationType.ERROR;
import static presentation.core.NotificationManager.NotificationType.INFO;

public class PortfolioViewModel
{
  private final PortfolioService portfolioService;
  private final StockTradingService tradingService;
  private final GameService gameService;
  private final NotificationManager notificationManager;

  private final DoubleProperty availableCash = new SimpleDoubleProperty();
  private final DoubleProperty totalWorth = new SimpleDoubleProperty();
  private final DoubleProperty netProfitLoss = new SimpleDoubleProperty();

  private final ObservableList<OwnedStockOverviewDTO> ownedStocksLiveList = FXCollections.observableArrayList();

  public PortfolioViewModel(PortfolioService portfolioService,
      StockTradingService tradingService,
      GameService gameService,
      NotificationManager notificationManager)
  {
    this.portfolioService = portfolioService;
    this.tradingService = tradingService;
    this.gameService = gameService;
    this.notificationManager = notificationManager;
  }

  public void loadPortfolioData(UUID portfolioId)
  {
    if (portfolioId == null) return;

    PortfolioOverviewDTO overview = portfolioService
        .getPortfolioOverview(portfolioId);
    availableCash.set(overview.balance());
    totalWorth.set(overview.totalPortfolioValue());

    double initialInvestment = 100000.00;
    netProfitLoss.set(overview.totalPortfolioValue() - initialInvestment);

    ownedStocksLiveList.setAll(portfolioService.getOwnedStocksDetails(portfolioId));
  }

  public void sellSelectedPosition(UUID portfolioId, OwnedStockOverviewDTO selectedAsset, int quantity)
  {
    if (portfolioId == null || selectedAsset == null)
    {
      notificationManager.show("Please select an asset position from your table first.", ERROR);
      return;
    }

    try
    {
      double executionPrice = selectedAsset.currentPrice();

      StockTransactionRequest request = new StockTransactionRequest(
          portfolioId, selectedAsset.symbol(), quantity);

      tradingService.sell(request);

      String receipt = String.format(
          "=== PORTFOLIO POSITION SOLD ===\n" +
              "• Order Type: SELL (from Portfolio)\n" +
              "• Asset: %s\n" +
              "• Quantity Sold: %d shares\n" +
              "• Execution Price: $%,.2f\n" +
              "• Total Credit Inbound: $%,.2f\n\n" +
              "Position updated successfully.",
          selectedAsset.symbol(), quantity, executionPrice, (executionPrice * quantity)
      );

      notificationManager.show(receipt, INFO);
      loadPortfolioData(portfolioId);
    }
    catch (Exception e)
    {
      notificationManager.show("Liquidation failed: " + e.getMessage(), ERROR);
    }
  }
  public UUID resetGame()
  {
    try
    {
      UUID newPortfolioId = gameService.startNewGame();

      loadPortfolioData(newPortfolioId);

      notificationManager.show("Game Reset Successful! All data wiped. Starting fresh with $100,000.00", INFO);

      return newPortfolioId;
    }
    catch (Exception e)
    {
      notificationManager.show("Failed to reset game: " + e.getMessage(), ERROR);
      return null;
    }
  }

  public DoubleProperty availableCashProperty() {return availableCash;}
  public DoubleProperty totalWorthProperty() {return totalWorth;}
  public DoubleProperty netProfitLossProperty(){return netProfitLoss;}

  public ObservableList<OwnedStockOverviewDTO> getOwnedStocksLiveList() {return ownedStocksLiveList;}
}