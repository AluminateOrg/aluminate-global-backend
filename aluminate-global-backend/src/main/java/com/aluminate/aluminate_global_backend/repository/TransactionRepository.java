package com.aluminate.aluminate_global_backend.repository;

import com.aluminate.aluminate_global_backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    /**
     * Finds a Transaction by its ID.
     *
     * @param id the ID of the Transaction
     * @return an Optional containing the Transaction if found, or empty if not found
     */
    Optional<Transaction> findById(Long id);
}
