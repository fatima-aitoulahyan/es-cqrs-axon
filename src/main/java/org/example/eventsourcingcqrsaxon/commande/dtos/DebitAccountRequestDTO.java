package org.example.eventsourcingcqrsaxon.commande.dtos;

public record DebitAccountRequestDTO(String accountId , double amount , String currency ) {
}
