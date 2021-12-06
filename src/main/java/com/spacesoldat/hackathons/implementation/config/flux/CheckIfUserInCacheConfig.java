package com.spacesoldat.hackathons.implementation.config.flux;

import com.spacesoldat.hackathons.caching.EntitiesCache;
import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import com.spacesoldat.hackathons.implementation.execution.logic.UserDataCheckImpl;
import com.spacesoldat.hackathons.streaming.transformers.flux.OneToManyValueTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class CheckIfUserInCacheConfig {

    @Autowired @Qualifier("clientsCache")
    private EntitiesCache clientsCache;

    @Bean(name="checkUserData")
    public OneToManyValueTransformer initCheckUserData(){
        Map<Class, Function> valueProcessors = new HashMap<>(){
            {
                put(
                        ClientPortfolioRequest.class,
                        UserDataCheckImpl.checkUserInCache(clientsCache)
                );
            }
        };

        return new OneToManyValueTransformer(
                "checkUserData",
                valueProcessors
        );
    }

}
