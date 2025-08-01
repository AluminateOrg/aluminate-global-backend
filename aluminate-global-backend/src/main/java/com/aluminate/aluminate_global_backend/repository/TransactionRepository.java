package com.aluminate.aluminate_global_backend.repository;

import com.aluminate.aluminate_global_backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Additional query methods can be defined here if needed


}
