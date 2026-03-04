package persistence.interfaces;

import domain.OwnedStock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OwnedStockDAO
{
  OwnedStock create(OwnedStock ownedStock);
  void update(OwnedStock ownedStock);
  boolean delete(UUID id);
  Optional<OwnedStock> getById(UUID id);
  List<OwnedStock> getAll();
}
