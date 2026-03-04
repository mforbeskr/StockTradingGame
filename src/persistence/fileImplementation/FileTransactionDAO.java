package persistence.fileImplementation;

import domain.Transaction;
import persistence.interfaces.TransactionDAO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileTransactionDAO implements TransactionDAO
{
  private final FileUnitOFWork uow;

  public FileTransactionDAO(FileUnitOFWork uow)
  {
    this.uow = uow;
  }

  @Override public Transaction create(Transaction transaction)
  {
    // Transactions go into the buffer to be appended during commit
    uow.getTransactionBuffer().add(transaction);
    return transaction;
  }

  @Override public Optional<Transaction> getById(UUID id)
  {
    return uow.getTransactions().stream().filter(t -> t.getId().equals(id))
        .findFirst();
  }

  @Override public List<Transaction> getAll()
  {
    return List.copyOf(uow.getTransactions());
  }
}