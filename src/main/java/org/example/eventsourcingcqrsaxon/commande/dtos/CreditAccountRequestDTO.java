package org.example.eventsourcingcqrsaxon.commande.dtos;

public record CreditAccountRequestDTO( String accountId , double amount ,String currency ) {
}
