package org.example.eventsourcingcqrsaxon.commande.aggregates;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import org.example.eventsourcingcqrsaxon.commande.commands.AddAccountCommand;
import org.example.eventsourcingcqrsaxon.commande.commands.CreditAccountCommand;
import org.example.eventsourcingcqrsaxon.commande.commands.DebitAccountCommand;
import org.example.eventsourcingcqrsaxon.events.AccountActivatedEvent;
import org.example.eventsourcingcqrsaxon.events.AccountCreatedEvent;
import org.example.eventsourcingcqrsaxon.enums.AccountStatus;
import org.example.eventsourcingcqrsaxon.events.AccountCreditedEvent;
import org.example.eventsourcingcqrsaxon.events.AccountDebitedEvent;

@Aggregate
@Slf4j
public class AccountAggregate {

    @AggregateIdentifier
    private String id;

    private double balance;

    private String currency;

    private AccountStatus status;

    public AccountAggregate() {
    }

    @CommandHandler
    public AccountAggregate(AddAccountCommand command) {
        log.info("************* AddAccountCommand Receved *******");


        if (command.getInitialBalance() < 0) {
            throw new IllegalArgumentException(
                    "Balance must be positive"
            );
        }

        AggregateLifecycle.apply(
                new AccountCreatedEvent(
                        command.getId(),
                        command.getInitialBalance(),
                        AccountStatus.CREATED,
                        command.getCurrency()
                )
        );
        AggregateLifecycle.apply(
                new AccountActivatedEvent(
                        command.getId(),
                        AccountStatus.ACTIVATED
                )
        );
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        log.info("************* AccountCreatedEvent    *******");

        this.id = event.getAccountId();
        this.balance = event.getInitialBalance();
        this.currency = event.getCurrency();
        this.status = AccountStatus.ACTIVATED;
    }

    @CommandHandler
    public void CreditAccountAggregate(CreditAccountCommand command) {
        log.info("************* CreditAccountAggregate Receved *******");
        if(!status.equals(AccountStatus.ACTIVATED)) throw new RuntimeException("Account not activated");


        if (command.getAmount() < 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive"
            );
        }

        AggregateLifecycle.apply(
                new AccountCreditedEvent(
                        command.getId(),
                        command.getAmount(),
                        AccountStatus.CREATED,
                        command.getCurrency()
                )
        );
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        log.info("************* AccountCreditedEvent    *******");

        this.id = event.getAccountId();
        this.balance = this.balance + event.getAmount();
        this.currency = event.getCurrency();
        this.status = AccountStatus.ACTIVATED;
    }

    @CommandHandler
    public void DebitAccountAggregate(DebitAccountCommand command) {
        log.info("************* DebitAccountAggregate Receved *******");
        if(!status.equals(AccountStatus.ACTIVATED)) throw new RuntimeException("Account not activated");
        if(this.balance< command.getAmount()) throw new RuntimeException("Balance is not suffisant ");


        if (command.getAmount() < 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive"
            );
        }

        AggregateLifecycle.apply(
                new AccountDebitedEvent(
                        command.getId(),
                        command.getAmount(),
                        AccountStatus.CREATED,
                        command.getCurrency()
                )
        );
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        log.info("************* AccountDebitedEvent    *******");

        this.id = event.getAccountId();
        this.balance = this.balance - event.getAmount();
        this.currency = event.getCurrency();
    }

}