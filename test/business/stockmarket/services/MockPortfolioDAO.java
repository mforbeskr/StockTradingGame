package business.stockmarket.services;

import domain.Portfolio;
import persistence.interfaces.PortfolioDAO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MockPortfolioDAO implements PortfolioDAO {
  private final MockUnitOfWork uow;

  public MockPortfolioDAO(MockUnitOfWork uow) {
    this.uow = uow;
  }

  @Override
  public Portfolio create(Portfolio p) {
    uow.getPortfolios().add(p);
    return p;
  }

  @Override
  public void update(Portfolio p) {
  }

  @Override
  public boolean delete(UUID id) {
    return uow.getPortfolios().removeIf(p -> p.getId().equals(id));
  }

  @Override
  public List<Portfolio> getAll() {
    return uow.getPortfolios();
  }

  @Override
  public Optional<Portfolio> getById(UUID id) {
    return uow.getPortfolios().stream().filter(p -> p.getId().equals(id)).findFirst();
  }
}