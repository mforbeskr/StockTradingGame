package business.stockmarket.services;

import domain.Transaction;
import persistence.interfaces.TransactionDAO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MockTransactionDAO implements TransactionDAO {
  private final MockUnitOfWork uow;

  public MockTransactionDAO(MockUnitOfWork uow) {
    this.uow = uow;
  }

  @Override
  public Transaction create(Transaction t) {
    uow.getTransactions().add(t);
    return t;
  }

  @Override
  public Optional<Transaction> getById(UUID id) {
    return uow.getTransactions().stream().filter(t -> t.getId().equals(id)).findFirst();
  }

  @Override
  public List<Transaction> getAll() {
    return uow.getTransactions();
  }

  @Override
  public void append(Transaction t) {
    uow.getTransactions().add(t);
  }

  @Override
  public void clearAll() {
    uow.getTransactions().clear();
  }
}