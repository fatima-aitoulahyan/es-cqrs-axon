package org.example.eventsourcingcqrsaxon.commande.aggregates;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import org.example.eventsourcingcqrsaxon.commande.commands.AddAccountCommand;
import org.example.eventsourcingcqrsaxon.events.AccountCreatedEvent;
import org.example.eventsourcingcqrsaxon.enums.AccountStatus;

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
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        log.info("************* AccountCreatedEvent    *******");

        this.id = event.getAccountId();
        this.balance = event.getInitialBalance();
        this.currency = event.getCurrency();
        this.status = AccountStatus.ACTIVATED;
    }
}