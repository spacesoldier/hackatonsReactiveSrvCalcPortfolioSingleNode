package com.spacesoldat.hackathons.implementation.config.flux;

import com.spacesoldat.hackathons.caching.EntitiesCache;
import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import com.spacesoldat.hackathons.implementation.execution.logic.CalculateUserPortfolioImpl;
import com.spacesoldat.hackathons.streaming.transformers.flux.OneToManyValueTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class CalcPortfolioIConfig {

    @Autowired @Qualifier("snapshotsUpdatesCache")
    private EntitiesCache snapshotsCache;

    @Autowired @Qualifier("incrementsCache")
    private EntitiesCache incrementsCache;


    @Bean(name="calculatePortfolio")
    public OneToManyValueTransformer calculatePortfolio(){
        Map<Class, Function> valueProcessors = new HashMap<>(){
            {
                put(
                        ClientPortfolioRequest.class,
                        CalculateUserPortfolioImpl.calculateUserPortfolio(
                                                                                snapshotsCache,
                                                                                incrementsCache
                                                                            )
                );
            }
        };
        return new OneToManyValueTransformer(
                "calculatePortfolio",
                valueProcessors
        );
    }

}
