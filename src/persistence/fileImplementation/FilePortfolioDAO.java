package persistence.fileImplementation;

import domain.Portfolio;
import persistence.interfaces.PortfolioDAO;
import shared.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FilePortfolioDAO implements PortfolioDAO
{
  private final FileUnitOFWork uow;

  public FilePortfolioDAO(FileUnitOFWork uow)
  {
    this.uow = uow;
  }

  @Override public Portfolio create(Portfolio portfolio)
  {
    uow.getPortfolios().add(portfolio);
    return portfolio;
  }

  @Override public void update(Portfolio portfolio)
  {
    List<Portfolio> list = uow.getPortfolios();
    for (int i = 0; i < list.size(); i++)
    {
      if (list.get(i).getId().equals(portfolio.getId()))
      {
        list.set(i, portfolio);
        System.out.println("Portfolio successfully updated!");
        return;
      }
    }
    Logger.getInstance().log("ERROR",
        "Update FAILED: Portfolio ID '" + portfolio.getId() + "' not found.");
    throw new RuntimeException(
        "Critical Update Error: Portfolio ID '" + portfolio.getId()
            + "' not found.");
  }

  @Override public boolean delete(UUID id)
  {
    boolean removed = uow.getPortfolios().removeIf(p -> p.getId().equals(id));
    if (!removed)
    {
      Logger.getInstance()
          .log("ERROR", "Delete FAILED: Portfolio ID '" + id + "' not found.");
    }
    return removed;
  }

  @Override public List<Portfolio> getAll()
  {
    return List.copyOf(uow.getPortfolios());
  }

  @Override public Optional<Portfolio> getById(UUID id)
  {
    for (Portfolio portfolios : uow.getPortfolios())
    {
      if (portfolios.getId().equals(id))
      {
        return Optional.of(portfolios);
      }
    }
    return Optional.empty();
  }
}
