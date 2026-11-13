package org.example.eventsourcingcqrsaxon.query.controllers;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.example.eventsourcingcqrsaxon.query.dtos.AccountStatementResponseDTO;
import org.example.eventsourcingcqrsaxon.query.entities.Account;
import org.example.eventsourcingcqrsaxon.query.entities.AccountOperation;
import org.example.eventsourcingcqrsaxon.query.queries.GetAccountStatementQuery;
import org.example.eventsourcingcqrsaxon.query.queries.GetAllAccountsQuery;
import org.example.eventsourcingcqrsaxon.query.queries.WatchEventQuery;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/query/accounts")
public class AccountQueryController {
    private QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }
    @GetMapping("/all")
    public CompletableFuture<List<Account>> getAllAccount(){
        CompletableFuture<List<Account>> response = queryGateway.query(
                new GetAllAccountsQuery(),
                ResponseTypes.multipleInstancesOf(Account.class));
        return response;

    }
    @GetMapping("/accountStatement{accountId}")
    public CompletableFuture<AccountStatementResponseDTO>  getAccountStatement(@PathVariable String accountId){
        CompletableFuture<AccountStatementResponseDTO> response = queryGateway.query(
                new GetAccountStatementQuery(accountId),
                ResponseTypes.instanceOf(AccountStatementResponseDTO.class));
        return response;

    }

    @GetMapping(value = "/watch/{accountId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AccountOperation> watch(@PathVariable String accountId){
        SubscriptionQueryResult<AccountOperation, AccountOperation> result =
                queryGateway.subscriptionQuery(new WatchEventQuery(accountId),
                        ResponseTypes.instanceOf(AccountOperation.class),
                        ResponseTypes.instanceOf(AccountOperation.class)
                );
        return result.initialResult().concatWith(result.updates());
    }

}
