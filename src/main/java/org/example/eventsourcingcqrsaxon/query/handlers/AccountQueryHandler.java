package org.example.eventsourcingcqrsaxon.query.handlers;

import org.axonframework.queryhandling.QueryHandler;
import org.example.eventsourcingcqrsaxon.query.entities.Account;
import org.example.eventsourcingcqrsaxon.query.queries.GetAllAccountsQuery;
import org.example.eventsourcingcqrsaxon.query.repository.AccountRepository;
import org.example.eventsourcingcqrsaxon.query.repository.OperationAccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountQueryHandler {
    private AccountRepository accountRepository;
    private OperationAccountRepository operationAccountRepository;

    public AccountQueryHandler(AccountRepository accountRepository, OperationAccountRepository operationAccountRepository) {
        this.accountRepository = accountRepository;

        this.operationAccountRepository = operationAccountRepository;
    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query){
        return accountRepository.findAll();
    }
}
