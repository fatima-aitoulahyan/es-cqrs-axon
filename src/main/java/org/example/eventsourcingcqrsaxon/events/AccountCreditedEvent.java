package org.example.eventsourcingcqrsaxon.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.eventsourcingcqrsaxon.enums.AccountStatus;

@Getter
@AllArgsConstructor
public class AccountCreditedEvent {
    private String accountId;
    private double amount;
    private AccountStatus status;
    private String currency;
}
