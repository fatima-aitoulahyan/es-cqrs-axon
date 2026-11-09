package org.example.eventsourcingcqrsaxon.commande.controllers;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.eventsourcingcqrsaxon.commande.dtos.AddNewAccountRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commands/accounts")
public class AccountCommandeController {
    @Autowired
    private CommandGateway commandGateway;
    @PostMapping("/add")

    public String addNewAccount(@RequestBody AddNewAccountRequestDTO requestDTO) {
        commandGateway.send()
    }
}
