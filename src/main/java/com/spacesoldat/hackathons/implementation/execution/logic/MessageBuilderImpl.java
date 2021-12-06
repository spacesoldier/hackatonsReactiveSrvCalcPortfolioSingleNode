package com.spacesoldat.hackathons.implementation.execution.logic;

import com.spacesoldat.hackathons.entities.internal.FluxRoutedEnvelope;
import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@Slf4j
public class MessageBuilderImpl {
    public static final String UNIT_NAME = "msg-builder";

    private static final String sysLogMsgTemplate = "[%s]: Received a request %s for %s";

    public static Function envelopeToRouting(String rqId, String streamName){

        String logMsgTemplate = "[REQUEST]: %s";
        return request -> {

                log.info(
                        String.format(logMsgTemplate,request.toString())
                );

                return FluxRoutedEnvelope
                            .builder()
                            .requestKey(rqId)
                            .requestObj(request)
                            .routeToAdapter(streamName)
                        .build();
        };
    }

    public static Function buildPortfolioRequest(String rqId, String login){

        return clientId -> {

            log.info(
                    String.format(
                                    sysLogMsgTemplate,
                                    UNIT_NAME,
                                    rqId,
                                    login
                            )
            );

            return ClientPortfolioRequest.builder()
                                            .rqId(rqId)
                                            .clientLogin(login)
                                         .build();
        };
    }

}
