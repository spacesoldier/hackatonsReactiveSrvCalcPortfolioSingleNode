package com.spacesoldat.hackathons.implementation.config.kafka;

import com.spacesoldat.hackathons.entities.kafka.SecurityLimitsRecord;
import com.spacesoldat.hackathons.implementation.execution.logic.ParseIncomingUpdatesImpl;
import com.spacesoldat.hackathons.implementation.execution.routing.kstream.KStreamToFluxForwardingImpl;
import com.spacesoldat.hackathons.streaming.manage.FluxWiresManager;
import com.spacesoldat.hackathons.streaming.transformers.kafka.LogicUnitKeyValueMapper;
import com.spacesoldat.hackathons.streaming.transformers.kafka.LogicUnitValueMapper;
import org.apache.kafka.streams.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Component
public class SecurityLimitsUpdatesConfig {

    @Autowired @Qualifier("FluxWiringManager")
    private FluxWiresManager fluxManager;


    @Bean(name = "parseUpdateMessage")
    public LogicUnitKeyValueMapper parseUpdateMessage(){
        // branch the message stream according to process state
        Map<Class, BiFunction<Object, Object, List<KeyValue<Object, Object>>>> valueProcessors = new HashMap<>(){
            {
                put(
                        String.class,
                        ParseIncomingUpdatesImpl.parseLimitsUpdate()
                );
            }
        };

        return new LogicUnitKeyValueMapper(
                valueProcessors,
                null,
                null,
                ParseIncomingUpdatesImpl.unitName
        );
    }

    @Bean(name="handleLimitsUpdates")
    public LogicUnitValueMapper handleLimitsUpdates(){
        Map<Class, BiFunction> valueInputProcessors = new HashMap<>(){
            {
                put(
                        SecurityLimitsRecord.class,
                        KStreamToFluxForwardingImpl.fromKstreamToFlux(
                                fluxManager.getSink("updates")
                        )
                );
            }
        };

        return new LogicUnitValueMapper(
                valueInputProcessors,
                null,
                null,
                KStreamToFluxForwardingImpl.unitName
        );
    }

}
