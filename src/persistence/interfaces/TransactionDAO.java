package persistence.interfaces;

import domain.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionDAO {
  Transaction create(Transaction transaction); // I chose to "leave it, just in case." but here I will make it append instead
  Optional<Transaction> getById(UUID id);
  List<Transaction> getAll();
  void append(Transaction transaction);
}