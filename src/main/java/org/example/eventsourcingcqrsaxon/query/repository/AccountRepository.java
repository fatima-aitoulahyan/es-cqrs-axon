package org.example.eventsourcingcqrsaxon.query.repository;

import org.example.eventsourcingcqrsaxon.query.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account , String> {
}
