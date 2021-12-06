package com.spacesoldat.hackathons.implementation.config.cache;

import com.spacesoldat.hackathons.caching.EntitiesCache;
import com.spacesoldat.hackathons.caching.EntityCacheStorePolicy;
import com.spacesoldat.hackathons.entities.external.ClientProfile;
import com.spacesoldat.hackathons.entities.kafka.SecurityLimitsRecord;
import com.spacesoldat.hackathons.entities.redis.TickerValue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class InternalCachesConfig {

    @Bean(name = "snapshotsUpdatesCache")
    public EntitiesCache prepareSnapshotsCache(){

        return EntitiesCache.builder()
                                .cacheName("snapshotsCache")
                                .storePolicy(EntityCacheStorePolicy.OVERWRITE_EXISTING)
                                .typeToStore(SecurityLimitsRecord.class)
                                .verbose(false)
                            .build();
    }


    @Bean(name="incrementsCache")
    public EntitiesCache prepareIncrementsCache(){
        return EntitiesCache.builder()
                                .cacheName("incrementsCache")
                                .storePolicy(EntityCacheStorePolicy.OVERWRITE_EXISTING)
                                .typeToStore(SecurityLimitsRecord.class)
                                .verbose(false)
                            .build();
    }


    @Bean(name="tickersCache")
    public EntitiesCache prepareTickersCache(){
        return EntitiesCache.builder()
                                .cacheName("tickersCache")
                                .storePolicy(EntityCacheStorePolicy.OVERWRITE_EXISTING)
                                .typeToStore(TickerValue.class)
                                .verbose(false)
                            .build();
    }


    @Bean(name="clientsCache")
    public EntitiesCache prepareClientsCache(){
        return EntitiesCache.builder()
                .cacheName("clientsCache")
                .storePolicy(EntityCacheStorePolicy.OVERWRITE_EXISTING)
                .typeToStore(ClientProfile.class)
                .verbose(false)
                .build();
    }
}
