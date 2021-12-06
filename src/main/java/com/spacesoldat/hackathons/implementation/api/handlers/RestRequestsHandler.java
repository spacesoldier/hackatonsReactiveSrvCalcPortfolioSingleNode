package com.spacesoldat.hackathons.implementation.api.handlers;

import com.spacesoldat.hackathons.implementation.execution.api.PortfolioHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RestRequestsHandler {

    @Autowired
    private PortfolioHandlerImpl clientsPortfolioService;

    public Mono<String> getClientsPortfolio(ServerWebExchange request) {

        return clientsPortfolioService.buildPortfolio(request);

    }
}
