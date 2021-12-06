package com.spacesoldat.hackathons.implementation.config.flux;

import com.spacesoldat.hackathons.entities.io.ClientPortfolioResponse;
import com.spacesoldat.hackathons.implementation.execution.logic.AggregateResponseImpl;
import com.spacesoldat.hackathons.streaming.manage.MonoWiresManager;
import com.spacesoldat.hackathons.streaming.transformers.flux.OneToOneValueTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class ResponseResolveLoopbackConfig {

    @Autowired @Qualifier("MonoWiringManager")
    private MonoWiresManager monoManager;

    @Bean(name = "sendResponseBodyToOutputResponse")
    public OneToOneValueTransformer sendResponseBodyToOutput(){
        Map<Class, Function> valueProcessors = new HashMap<>(){
            {
                put(
                        ClientPortfolioResponse.class,
                        AggregateResponseImpl.sendResponseBodyForSuccessResult(monoManager)
                );
            }
        };
        return new OneToOneValueTransformer(
                "sendResponseBodyToOutput",
                valueProcessors
        );
    }
}
