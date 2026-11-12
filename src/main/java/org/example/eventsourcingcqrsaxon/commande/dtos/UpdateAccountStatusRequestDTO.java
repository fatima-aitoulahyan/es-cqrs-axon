package org.example.eventsourcingcqrsaxon.commande.dtos;

import org.example.eventsourcingcqrsaxon.enums.AccountStatus;

public record UpdateAccountStatusRequestDTO(String accountId , AccountStatus accountStatus) {
}
