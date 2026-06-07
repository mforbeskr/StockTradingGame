package business.stockmarket.services;

import business.dto.OwnedStockOverviewDTO;
import business.dto.PortfolioOverviewDTO;
import domain.OwnedStock;
import domain.Portfolio;
import domain.Stock;
import persistence.interfaces.OwnedStockDAO;
import persistence.interfaces.PortfolioDAO;
import persistence.interfaces.StockDAO;
import persistence.interfaces.TransactionDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PortfolioService
{
  private final PortfolioDAO portfolioDAO;
  private final StockDAO stockDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;

  public PortfolioService(PortfolioDAO portfolioDAO, StockDAO stockDAO,
      OwnedStockDAO ownedStockDAO, TransactionDAO transactionDAO)
  {
    this.portfolioDAO = portfolioDAO;
    this.stockDAO = stockDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
  }

  public PortfolioOverviewDTO getPortfolioOverview(UUID portfolioId)
  {
    Portfolio portfolio = portfolioDAO.getById(portfolioId)
        .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));

    double balance = portfolio.getCurrentBalance();

    List<OwnedStockOverviewDTO> ownedStocksDetails = getOwnedStocksDetails(
        portfolioId);

    double totalStockValue = ownedStocksDetails.stream()
        .mapToDouble(OwnedStockOverviewDTO::totalValue).sum();

    return new PortfolioOverviewDTO(balance, totalStockValue,
        balance + totalStockValue);
  }

  public List<OwnedStockOverviewDTO> getOwnedStocksDetails(UUID portfolioId)
  {
    List<OwnedStockOverviewDTO> result = new ArrayList<>();

    List<OwnedStock> ownedStocks = getOwnedStocksForPortfolio(portfolioId);

    for (OwnedStock os : ownedStocks)
    {
      Stock stock = stockDAO.getBySymbol(os.getStockSymbol()).orElse(null);
      if (stock != null)
      {
        double currentPrice = stock.getCurrentPrice();
        double totalValue = os.getNumberOfShares() * currentPrice;

        result.add(new OwnedStockOverviewDTO(os.getStockSymbol(),
            os.getNumberOfShares(), currentPrice, totalValue));
      }
    }
    return result;
  }

  public List<domain.Transaction> getTransactionHistory(UUID portfolioId)
  {
    return transactionDAO.getAll().stream()
        .filter(t -> t.getPortfolioId().equals(portfolioId)).toList();
  }

  public double getTotalNetProfitLoss(UUID portfolioId)
  {
    List<domain.Transaction> history = getTransactionHistory(portfolioId);
    double netProfitLoss = 0;

    for (domain.Transaction t : history)
    {
      double totalAmount = t.getPricePerShare() * t.getQuantity();

      if (t.getType() == domain.TransactionType.BUY)
      {
        netProfitLoss -= (totalAmount + t.getFee());
      }
      else if (t.getType() == domain.TransactionType.SELL)
      {
        netProfitLoss += (totalAmount - t.getFee());
      }
    }
    return netProfitLoss;
  }

  private List<OwnedStock> getOwnedStocksForPortfolio(UUID portfolioId)
  {
    return ownedStockDAO.getAll().stream()
        .filter(os -> os.getPortfolioId().equals(portfolioId)).toList();
  }

}
