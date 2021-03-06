package com.spacesoldat.hackathons.implementation.execution.routing.flux;

import com.spacesoldat.hackathons.entities.internal.FluxRoutedEnvelope;
import com.spacesoldat.hackathons.streaming.manage.FluxWiresManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class FluxChainsRoutingImpl {
    public static final String unitName = "object router";
    private static final Logger logger = LoggerFactory.getLogger("flux to flux");

    public static Function<FluxRoutedEnvelope,String> routeEnvelopeToFlux(
            FluxWiresManager fluxManager
    ){
        String logMsgTemplate = "route %s to %s";
        return envelope -> {

            String logMsg = null;
            if (envelope.getRouteToAdapter() != null){
                fluxManager.getSink(
                        envelope.getRouteToAdapter()
                ).accept(
                        envelope.getRequestObj()
                );
                logMsg = String.format(
                        logMsgTemplate,
                        envelope.getRequestObj().getClass().getName(),
                        envelope.getRouteToAdapter()
                );

                //for minimalistic tracing
                //logger.info(logMsg);

            } else {
                logMsg = "[ROUTING ERROR]: flux name was not set";
            }

            return logMsg;
        };
    }
}
