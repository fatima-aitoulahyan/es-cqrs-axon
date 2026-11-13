package org.example.eventsourcingcqrsaxon.query.dtos;

import org.example.eventsourcingcqrsaxon.query.entities.Account;
import org.example.eventsourcingcqrsaxon.query.entities.AccountOperation;

import java.util.List;

public record AccountStatementResponseDTO (Account account , List<AccountOperation> operations){
}
