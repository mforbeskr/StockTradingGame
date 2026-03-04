package persistence.interfaces;

import domain.Portfolio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioDAO
{
  Portfolio create(Portfolio portfolio);
  void update(Portfolio portfolio);
  boolean delete(UUID id);
  List<Portfolio> getAll();
  Optional<Portfolio> getById(UUID id); // symbols identify stocks
}
