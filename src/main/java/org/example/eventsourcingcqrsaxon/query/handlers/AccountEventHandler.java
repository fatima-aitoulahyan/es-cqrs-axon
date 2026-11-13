package org.example.eventsourcingcqrsaxon.query.handlers;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.example.eventsourcingcqrsaxon.events.AccountActivatedEvent;
import org.example.eventsourcingcqrsaxon.events.AccountCreditedEvent;
import org.example.eventsourcingcqrsaxon.query.entities.Account;
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

    @EventHandler
    public void on(AccountCreditedEvent event , EventMessage eventMessage){
        Account account = Account.builder()
                .id(event.getAccountId())
                .accountStatus(event.getStatus())
                .balance(event.getAmount())
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
}
