package com.spacesoldat.hackathons.implementation.config.flux;

import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import com.spacesoldat.hackathons.implementation.execution.logic.MockPortfolioImpl;
import com.spacesoldat.hackathons.streaming.transformers.flux.OneToManyValueTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class MockResponsesStage {

    @Bean(name="mockClientPortfolio")
    public OneToManyValueTransformer mockClientPortfolio(){
        Map<Class, Function> valueProcessors = new HashMap<>(){
            {
                put(
                        ClientPortfolioRequest.class,
                        MockPortfolioImpl.mockClientPortfolio()
                );
            }
        };
        return new OneToManyValueTransformer(
                "mockPortfolio",
                valueProcessors
        );
    }


}
