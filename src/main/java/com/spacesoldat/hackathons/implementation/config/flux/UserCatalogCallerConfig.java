package com.spacesoldat.hackathons.implementation.config.flux;

import com.spacesoldat.hackathons.entities.internal.queries.FindUserInCatalogRequest;
import com.spacesoldat.hackathons.entities.internal.queries.RunUserCatalogRequest;
import com.spacesoldat.hackathons.implementation.execution.logic.UserCatalogCallLogicImpl;
import com.spacesoldat.hackathons.streaming.transformers.flux.OneToManyValueTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class UserCatalogCallerConfig {

    @Autowired @Qualifier("userCatalogClient")
    private WebClient userCatalogClient;

    @Bean(name="prepareCallSpec")
    public OneToManyValueTransformer prepareRequestSpec(){
        Map<Class, Function> valueProcessors = new HashMap<>(){
            {
                put(
                        FindUserInCatalogRequest.class,
                        UserCatalogCallLogicImpl.prepareCallSpecImpl(userCatalogClient)
                );
            }
        };

        return new OneToManyValueTransformer(
                "prepareCallSpec",
                valueProcessors
        );
    }

    @Autowired @Qualifier("onFailCatalogRsHandler")
    private BiConsumer onFailConsumer;

    @Autowired @Qualifier("onSuccessCatalogRsHandler")
    private BiConsumer onSuccessConsumer;

    @Bean(name="performCatalogCall")
    public OneToManyValueTransformer performCatalogCall(){
        Map<Class, Function> valueProcessors = new HashMap<>(){
            {
                put(
                        RunUserCatalogRequest.class,
                        UserCatalogCallLogicImpl.runCatalogCall(onSuccessConsumer,onFailConsumer)
                );
            }
        };

        return new OneToManyValueTransformer(
                "prepareCallSpec",
                valueProcessors
        );
    }

}
