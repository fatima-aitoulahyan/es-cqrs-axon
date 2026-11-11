package org.example.eventsourcingcqrsaxon.commande.controllers;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.example.eventsourcingcqrsaxon.commande.commands.AddAccountCommand;
import org.example.eventsourcingcqrsaxon.commande.commands.CreditAccountCommand;
import org.example.eventsourcingcqrsaxon.commande.dtos.AddNewAccountRequestDTO;
import org.example.eventsourcingcqrsaxon.commande.dtos.CreditAccountRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/commands/accounts")
public class AccountCommandeController {
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private EventStore eventStore;

    @PostMapping("/add")
    public CompletableFuture<String> addNewAccount(@RequestBody AddNewAccountRequestDTO requestDTO) {
        CompletableFuture<String> response = commandGateway.send(new AddAccountCommand(
                UUID.randomUUID().toString(),
                requestDTO.initialBalance(),
                requestDTO.currency()
        ));
        return response;
    }

    @PostMapping("/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDTO requestDTO) {
        CompletableFuture<String> response = commandGateway.send(new CreditAccountCommand(
                requestDTO.accountId(),
                requestDTO.amount(),
                requestDTO.currency()
        ));
        return response;
    }

    @ExceptionHandler(Exception.class)
    public String execeptionHandeler(Exception exception){
        return exception.getMessage();
    }

    @GetMapping("/events/{accountId}")
    public String eventsStore(@PathVariable String accountId){
        return eventStore.readEvents(accountId).asStream().toString();
    }
}
