package org.example.eventsourcingcqrsaxon.commande.aggregates;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.example.eventsourcingcqrsaxon.commande.commands.AddAccountCommand;
import org.example.eventsourcingcqrsaxon.enums.AccountStatus;
@Aggregate
public class AccountAggregate {
    private String accountd;
    private double balance;
    private AccountStatus status;

    @CommandHandler
    public AccountAggregate(AddAccountCommand command){

        if(command.getInitialBalance()<0) throw new IllegalArgumentException("Balance must be positive");
    }
}
