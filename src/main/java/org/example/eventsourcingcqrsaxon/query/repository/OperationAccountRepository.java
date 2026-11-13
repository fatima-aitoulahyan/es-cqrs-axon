package org.example.eventsourcingcqrsaxon.query.repository;

import org.example.eventsourcingcqrsaxon.query.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationAccountRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findByAccountId(String accountId);
}
