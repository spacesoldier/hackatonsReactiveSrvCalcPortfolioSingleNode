package com.spacesoldat.hackathons.implementation.config.redis;

import com.spacesoldat.hackathons.entities.io.ClientPortfolioResponse;
import com.spacesoldat.hackathons.implementation.execution.logic.MockPortfolioImpl;
import com.spacesoldat.hackathons.streaming.transformers.flux.OneToManyValueTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class RedisTickerUpdateConfig {

    @Bean(name="updateTickersInPortfolio")
    public OneToManyValueTransformer updateTickerValues(){
        Map<Class, Function> valueProcessors = new HashMap<>(){
            {
                put(
                        ClientPortfolioResponse.class,
                        MockPortfolioImpl.mockClientPortfolio()
                );
            }
        };
        return new OneToManyValueTransformer(
                "updateTickersInPortfolio",
                valueProcessors
        );


    }

}
