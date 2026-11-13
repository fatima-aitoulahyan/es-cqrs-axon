package org.example.eventsourcingcqrsaxon.query.handlers;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.example.eventsourcingcqrsaxon.commande.commands.UpdateAccountStatusCommand;
import org.example.eventsourcingcqrsaxon.enums.OperationType;
import org.example.eventsourcingcqrsaxon.events.*;
import org.example.eventsourcingcqrsaxon.query.entities.Account;
import org.example.eventsourcingcqrsaxon.query.entities.AccountOperation;
import org.example.eventsourcingcqrsaxon.query.repository.AccountRepository;
import org.example.eventsourcingcqrsaxon.query.repository.OperationAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountEventHandler {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OperationAccountRepository operationAccountRepository;
    private QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void on(AccountCreatedEvent event , EventMessage eventMessage){
        Account account = Account.builder()
                .id(event.getAccountId())
                .accountStatus(event.getStatus())
                .balance(event.getInitialBalance())
                .currency(event.getCurrency())
                .createdAt(eventMessage.getTimestamp())
                .build();
        accountRepository.save(account);

    }


    @EventHandler
    public void on(AccountActivatedEvent event){
        Account account = accountRepository.findById(event.getAccountId()).orElseThrow();
        account.setAccountStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountStatusUpdatedEvent event){
        Account account = accountRepository.findById(event.getAccountId()).orElseThrow();
        account.setAccountStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountDebitedEvent event , EventMessage eventMessage){
        Account account = accountRepository.findById(event.getAccountId()).orElseThrow();
        AccountOperation accountOperation = AccountOperation.builder()
                .amount(event.getAmount())
                .createdAt(eventMessage.getTimestamp())
                .operationType(OperationType.DEBIT)
                .currency(event.getCurrency())
                .account(account)
                .build();
        operationAccountRepository.save(accountOperation);
        account.setBalance(account.getBalance() - event.getAmount());
        accountRepository.save(account);
        queryUpdateEmitter.emit(e->true,accountOperation);
    }
    @EventHandler
    public void on(AccountCreditedEvent event , EventMessage eventMessage){
        Account account = accountRepository.findById(event.getAccountId()).orElseThrow();
        AccountOperation accountOperation = AccountOperation.builder()
                .amount(event.getAmount())
                .createdAt(eventMessage.getTimestamp())
                .operationType(OperationType.CREDIT)
                .currency(event.getCurrency())
                .account(account)
                .build();
        operationAccountRepository.save(accountOperation);
        account.setBalance(account.getBalance() + event.getAmount());
        accountRepository.save(account);
        queryUpdateEmitter.emit(e->true,accountOperation);

    }
}
