package persistence.fileImplementation;

import domain.OwnedStock;
import persistence.interfaces.OwnedStockDAO;
import shared.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileOwnedStockDAO implements OwnedStockDAO
{
  private final FileUnitOFWork uow;


  public FileOwnedStockDAO(FileUnitOFWork uow)
  {
    this.uow = uow;
  }

  @Override public OwnedStock create(OwnedStock ownedStock)
  {
    uow.getOwnedStocks().add(ownedStock);
    return ownedStock;
  }

  @Override public void update(OwnedStock ownedStock)
  {
    List<OwnedStock> list = uow.getOwnedStocks();
    for (int i = 0; i < list.size(); i++)
    {
      if (list.get(i).getId().equals(ownedStock.getId()))
      {
        list.set(i, ownedStock);
        System.out.println("OwnedStock successfully updated!");
        return;
      }
    }
    // When/if the OwnedStock isn't found
    Logger.getInstance().log("ERROR",
        "Update FAILED: OwnedStock ID '" + ownedStock.getId() + "' not found.");
    throw new RuntimeException(
        "Critical Update Error: OwnedStock '" + ownedStock.getId()
            + "' not found.");
  }

  @Override public boolean delete(UUID id)
  {
    boolean removed = uow.getOwnedStocks()
        .removeIf(os -> os.getId().equals(id));
    if (!removed)
    {
      Logger.getInstance()
          .log("ERROR", "Delete FAILED: OwnedStock ID '" + id + "' not found.");
    }
    return removed;
  }

  @Override public Optional<OwnedStock> getById(UUID id)
  {
    return uow.getOwnedStocks().stream().filter(os -> os.getId().equals(id))
        .findFirst();
  }

  @Override public List<OwnedStock> getAll()
  {
    return List.copyOf(uow.getOwnedStocks());
  }

  // taken help from 'outside' to create this method... was unsure how to use stream
  public List<OwnedStock> getByPortfolioId(UUID portfolioId)
  {
    return uow.getOwnedStocks().stream()
        .filter(os -> os.getPortfolioId().equals(portfolioId))
        .collect(Collectors.toList());
  }
}
