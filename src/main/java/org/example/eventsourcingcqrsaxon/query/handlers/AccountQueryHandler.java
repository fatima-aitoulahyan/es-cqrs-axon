package org.example.eventsourcingcqrsaxon.query.handlers;

import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.example.eventsourcingcqrsaxon.query.dtos.AccountStatementResponseDTO;
import org.example.eventsourcingcqrsaxon.query.entities.Account;
import org.example.eventsourcingcqrsaxon.query.entities.AccountOperation;
import org.example.eventsourcingcqrsaxon.query.queries.GetAccountStatementQuery;
import org.example.eventsourcingcqrsaxon.query.queries.GetAllAccountsQuery;
import org.example.eventsourcingcqrsaxon.query.repository.AccountRepository;
import org.example.eventsourcingcqrsaxon.query.repository.OperationAccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountQueryHandler {
    private final QueryGateway queryGateway;
    private AccountRepository accountRepository;
    private OperationAccountRepository operationAccountRepository;

    public AccountQueryHandler(AccountRepository accountRepository, OperationAccountRepository operationAccountRepository, QueryGateway queryGateway) {
        this.accountRepository = accountRepository;

        this.operationAccountRepository = operationAccountRepository;
        this.queryGateway = queryGateway;
    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query){
        return accountRepository.findAll();
    }

    @QueryHandler
    public AccountStatementResponseDTO on(GetAccountStatementQuery query){
        Account account = accountRepository.findById(query.getAccountId()).orElseThrow();
        List<AccountOperation> operations=operationAccountRepository.findByAccountId(account.getId());
        return new AccountStatementResponseDTO(account,operations);
    }


}
