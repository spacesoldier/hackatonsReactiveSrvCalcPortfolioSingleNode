package com.spacesoldat.hackathons.implementation.execution.api;

import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import com.spacesoldat.hackathons.streaming.manage.FluxWiresManager;
import com.spacesoldat.hackathons.streaming.manage.MonoWiresManager;
import com.spacesoldat.hackathons.streaming.transformers.flux.OneToOneValueTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class PortfolioHandlerImpl {

    @Autowired @Qualifier("routeToFluxByName")
    private OneToOneValueTransformer routeToFluxByName;

    @Autowired @Qualifier("MonoWiringManager")
    private MonoWiresManager monoManager;

    @Autowired @Qualifier("FluxWiringManager")
    private FluxWiresManager fluxManager;

    public Mono<String> buildPortfolio(ServerWebExchange exchange) {

        // let's mark every request with its very own id
        String rqId = UUID.randomUUID().toString();

        String userLoginWithSlash = exchange.getRequest().getPath().subPath(2).value();

        String userLogin = userLoginWithSlash.substring(1);

        monoManager.newWire(rqId);

        // wrap request id together with, well, request
        ClientPortfolioRequest incomingRequest = ClientPortfolioRequest.builder()
                                                    .rqId(rqId)
                                                    .serverWebExchange(exchange)
                                                    .clientLogin(userLogin)
                                                .build();

        // drop the message into our reactive hell
        fluxManager.getSink("serve").accept(incomingRequest);

        return monoManager.getOutput(rqId);
    }
}
