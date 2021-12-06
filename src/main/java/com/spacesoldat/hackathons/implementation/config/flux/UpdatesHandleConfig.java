package com.spacesoldat.hackathons.implementation.config.flux;

import com.spacesoldat.hackathons.caching.EntitiesCache;
import com.spacesoldat.hackathons.entities.external.ClientProfile;
import com.spacesoldat.hackathons.entities.kafka.SecurityLimitsRecord;
import com.spacesoldat.hackathons.entities.redis.TickerValue;
import com.spacesoldat.hackathons.implementation.execution.logic.HandleCachesUpdatesImpl;
import com.spacesoldat.hackathons.streaming.transformers.flux.OneToOneValueTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class UpdatesHandleConfig {

    @Autowired @Qualifier("snapshotsUpdatesCache")
    private EntitiesCache snapshotsCache;

    @Autowired @Qualifier("incrementsCache")
    private EntitiesCache incrementsCache;

    @Autowired @Qualifier("tickersCache")
    private EntitiesCache tickersCache;

    @Autowired @Qualifier("clientsCache")
    private EntitiesCache clientsCache;

    @Bean(name="saveUpdatesToCaches")
    public OneToOneValueTransformer saveUpdatesToCaches(){
        Map<Class, Function> valueProcessors = new HashMap<>(){
            {
                put(
                        SecurityLimitsRecord.class,
                        HandleCachesUpdatesImpl.saveSnapshotOrIncrement(snapshotsCache, incrementsCache)
                );
                put(
                        TickerValue.class,
                        HandleCachesUpdatesImpl.saveTicker(tickersCache)
                );
                put(
                        ClientProfile.class,
                        HandleCachesUpdatesImpl.saveUser(clientsCache)
                );
            }
        };

        return new OneToOneValueTransformer(
                "saveUpdatesToCache",
                valueProcessors
        );


    }
}
