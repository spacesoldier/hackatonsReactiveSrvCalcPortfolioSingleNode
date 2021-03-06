package com.spacesoldat.hackathons.implementation.api;

import com.spacesoldat.hackathons.implementation.api.handlers.RestRequestsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@RestController
public class ClientsPortfolioApi {

    @Autowired
    RestRequestsHandler requestsHandler;

    @RequestMapping(value="/portfolio/{login}", method = RequestMethod.GET)
    public Mono<String> testApiCall(
            ServerWebExchange exchange
    ){
        // provide the Mono object to the framework for response subscription
        return requestsHandler.getClientsPortfolio(exchange);
    }

}
