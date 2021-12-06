package com.spacesoldat.hackathons.implementation;

import com.spacesoldat.hackathons.streaming.manage.FluxWiresManager;
import com.spacesoldat.hackathons.streaming.transformers.flux.OneToManyValueTransformer;
import com.spacesoldat.hackathons.streaming.transformers.flux.OneToOneValueTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FluxLogicConnections {

    @Autowired @Qualifier("FluxWiringManager")
    private FluxWiresManager fluxManager;

    @Autowired @Qualifier("mockClientPortfolio")
    private OneToManyValueTransformer mockPortfolio;

    @Autowired @Qualifier("routeToFluxByName")
    private OneToOneValueTransformer routeToFluxByName;

    @Autowired @Qualifier("sendResponseBodyToOutputResponse")
    private OneToOneValueTransformer sendResponseBodyToOutput;

    @Autowired @Qualifier("saveUpdatesToCaches")
    private OneToOneValueTransformer saveUpdatesToCaches;

    @Autowired @Qualifier("checkUserData")
    private OneToManyValueTransformer checkUserData;

    @Autowired @Qualifier("prepareCallSpec")
    private OneToManyValueTransformer prepareCallSpec;

    @Autowired @Qualifier("performCatalogCall")
    private OneToManyValueTransformer performCatalogCall;

    @Autowired @Qualifier("calculatePortfolio")
    private OneToManyValueTransformer calculatePortfolio;

    // This is the heart of our service - its main configuration which describes connections
    // between the small parts of its logic
    // If you read this - congratulations! You're on the right way ;)
    @Bean(name="configMainLogicNode")
    public void configMainFluxChain(){

        // here we handle snapshots, increments, tickers and users updates
        // not too much - just put them into the cache
        fluxManager.getStream("updates")
                .map(   saveUpdatesToCaches    )
                .subscribe();

        // here we call the user catalog service
        fluxManager.getStream("user_catalog_call")
                .flatMap(   prepareCallSpec         )
                .flatMap(    performCatalogCall     )
                .subscribe();

        // main logic flow - serve portfolio requests
        fluxManager.getStream("serve"     )
                //.flatMap    (   mockPortfolio                   )
                .flatMap    (   checkUserData                   )
                .flatMap    (   calculatePortfolio              )
                .map        (   sendResponseBodyToOutput        )
                .map        (   routeToFluxByName               )
                .subscribe();


    }

}
