package persistence.interfaces;

import domain.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionDAO {
  Transaction create(Transaction transaction);
  Optional<Transaction> getById(UUID id);
  List<Transaction> getAll();
}