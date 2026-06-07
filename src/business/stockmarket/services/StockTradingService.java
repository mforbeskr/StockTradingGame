package business.stockmarket.services;

import business.dto.StockTransactionRequest;
import business.stockmarket.strategies.FeeCalculationStrategy;
import domain.*;
import persistence.interfaces.*;
import shared.logging.Logger;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class StockTradingService
{

  private final UnitOfWork uow;
  private final PortfolioDAO portfolioDAO;
  private final StockDAO stockDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;
  private final FeeCalculationStrategy feeStrategy;

  public StockTradingService(UnitOfWork uow, PortfolioDAO portfolioDAO,
      StockDAO stockDAO, OwnedStockDAO ownedStockDAO,
      TransactionDAO transactionDAO, FeeCalculationStrategy feeStrategy)
  {
    this.uow = uow;
    this.portfolioDAO = portfolioDAO;
    this.stockDAO = stockDAO;
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
    this.feeStrategy = feeStrategy;
  }

  public void sell(StockTransactionRequest request)
  {
    try
    {
      uow.beginTransaction();

      UUID portfolioID = request.portfolioId();
      String symbol = request.symbol();
      int quantity = request.quantity();

      ensureQuantityAmountIsValid(quantity);

      Portfolio portfolio = getPortfolio(portfolioID);
      Stock stock = getStock(symbol);

      OwnedStock ownedStock = ownedStockDAO.getByPortfolioIdAndStockSymbol(
          portfolioID, symbol).orElseThrow(
          () -> new IllegalArgumentException("No shares found for: " + symbol));

      int numberOfShares = ownedStock.getNumberOfShares();

      ensureNumberOfSharesIsEqualOrMoreThanQuantity(numberOfShares, quantity);

      double pricePerShare = stock.getCurrentPrice();
      double sharesPrice = pricePerShare * quantity;

      double transactionFee = feeStrategy.calculateFee(quantity, pricePerShare);

      double finalPrice = sharesPrice - transactionFee;

      portfolio.setCurrentBalance(portfolio.getCurrentBalance() + finalPrice);
      portfolioDAO.update(portfolio);

      int remainingShares = numberOfShares - quantity;

      if (remainingShares == 0)
      {
        ownedStockDAO.delete(ownedStock.getId());
      }
      else
      {
        ownedStock.setNumberOfShares(remainingShares);
        ownedStockDAO.update(ownedStock);
      }

      Transaction transaction = new Transaction(portfolioID, symbol,
          Instant.now(), transactionFee, pricePerShare, quantity,
          TransactionType.SELL);
      transactionDAO.create(transaction);
      Logger.getInstance().log("INFO",
          "SUCCESS: Sold " + quantity + " shares of " + symbol + ".");
      uow.commit();
    }
    catch (Exception e)
    {
      uow.rollback();
      Logger.getInstance()
          .log("ERROR", "Failed to process sell order: " + e.getMessage());
      throw new RuntimeException("Failed to process sell order", e);
    }
  }

  private void ensureNumberOfSharesIsEqualOrMoreThanQuantity(int shares,
      int quantity)
  {
    if (shares < quantity)
    {
      throw new IllegalArgumentException(
          "Not enough shares. Owned :" + shares + ", Requested: " + quantity);
    }
  }

  public void buy(StockTransactionRequest request)
  {
    try
    {
      uow.beginTransaction();

      UUID portfolioID = request.portfolioId();
      String symbol = request.symbol();
      int quantity = request.quantity();

      ensureQuantityAmountIsValid(quantity);

      Portfolio portfolio = getPortfolio(portfolioID);
      Stock stock = getStock(symbol);

      ensureStockIsNotBankrupt(stock);

      double pricePerShare = stock.getCurrentPrice();
      double sharesPrice = pricePerShare * quantity;

      double transactionFee = feeStrategy.calculateFee(quantity, pricePerShare);

      double finalPrice = sharesPrice + transactionFee;

      ensurePortfolioHasSufficientFunds(portfolio, finalPrice);

      portfolio.setCurrentBalance(portfolio.getCurrentBalance() - finalPrice);
      portfolioDAO.update(portfolio);

      Optional<OwnedStock> optionalOwnedStock = ownedStockDAO.getByPortfolioIdAndStockSymbol(
          portfolioID, symbol);

      if (optionalOwnedStock.isEmpty())
      {
        OwnedStock ownedStock = new OwnedStock(quantity, portfolioID, symbol);
        ownedStockDAO.create(ownedStock);
      }
      else
      {
        OwnedStock ownedStock = optionalOwnedStock.get();
        ownedStock.setNumberOfShares(ownedStock.getNumberOfShares() + quantity);
        ownedStockDAO.update(ownedStock);
      }
      Transaction transaction = new Transaction(portfolioID, symbol,
          Instant.now(), transactionFee, pricePerShare, quantity,
          TransactionType.BUY);
      transactionDAO.create(transaction);

      Logger.getInstance().log("INFO",
          "SUCCESSFUL PURCHASE: Bought " + quantity + " shares of " + symbol
              + " for portfolio: " + portfolioID);
      uow.commit();
    }
    catch (Exception e)
    {
      uow.rollback();
      Logger.getInstance()
          .log("ERROR", "Failed to process buy order: " + e.getMessage());
      throw new RuntimeException("Failed to process buy order", e);
    }
  }

  private void ensurePortfolioHasSufficientFunds(Portfolio portfolio,
      double finalPrice)
  {
    if (portfolio.getCurrentBalance() < finalPrice)
    {
      throw new IllegalArgumentException("You are too broke for this");
    }
  }

  private void ensureStockIsNotBankrupt(Stock stock)
  {
    if (stock.getCurrentState() == State.BANKRUPT)
    {
      throw new IllegalArgumentException(
          "Stock is bankrupt and cannot be purchased");
    }
  }

  private void ensureQuantityAmountIsValid(int quantity)
  {
    if (quantity < 1)
    {
      throw new IllegalArgumentException("Quantity cannot be lower than 1");
    }
  }

  private Stock getStock(String symbol)
  {
    return stockDAO.getBySymbol(symbol).orElseThrow(
        () -> new IllegalArgumentException("No stock found with " + symbol));
  }

  private Portfolio getPortfolio(UUID portfolioId)
  {
    return portfolioDAO.getById(portfolioId).orElseThrow(
        () -> new IllegalArgumentException(
            "No portfolio found with " + portfolioId));
  }
}
