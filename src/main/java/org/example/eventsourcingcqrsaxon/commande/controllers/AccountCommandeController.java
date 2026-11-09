package org.example.eventsourcingcqrsaxon.commande.controllers;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.eventsourcingcqrsaxon.commande.commands.AddAccountCommand;
import org.example.eventsourcingcqrsaxon.commande.dtos.AddNewAccountRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/commands/accounts")
public class AccountCommandeController {
    @Autowired
    private CommandGateway commandGateway;
    @PostMapping("/add")

    public CompletableFuture<String> addNewAccount(@RequestBody AddNewAccountRequestDTO requestDTO) {
        CompletableFuture<String> response = commandGateway.send(new AddAccountCommand(
                UUID.randomUUID().toString(),
                requestDTO.initialBalance(),
                requestDTO.currency()
        ));
        return response;
    }
    @ExceptionHandler(Exception.class)
    public String execeptionHandeler(Exception exception){
        return exception.getMessage();
    }
}
