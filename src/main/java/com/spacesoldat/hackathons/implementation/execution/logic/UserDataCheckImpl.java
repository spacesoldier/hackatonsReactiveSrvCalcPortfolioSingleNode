package com.spacesoldat.hackathons.implementation.execution.logic;

import com.spacesoldat.hackathons.caching.EntitiesCache;
import com.spacesoldat.hackathons.entities.external.ClientProfile;
import com.spacesoldat.hackathons.entities.internal.FluxRoutedEnvelope;
import com.spacesoldat.hackathons.entities.internal.queries.BuildClientsPortfolioRequest;
import com.spacesoldat.hackathons.entities.internal.queries.FindUserInCatalogRequest;
import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class UserDataCheckImpl {

    public static final String unitName = "check-user-known";
    private static final Logger logger = LoggerFactory.getLogger(unitName);

    public static Function<ClientPortfolioRequest, List> checkUserInCache(EntitiesCache usersCache){

        String userExistsMsgTemplate = "[%s]: user % already in cache";
        String requestUserFromCatalogMsgTemplate = "[$s]: need to request user %s from catalog";

        return request -> {

            List results = new ArrayList();

            String userLogin = request.getClientLogin();

            ClientProfile clientProfile = (ClientProfile) usersCache.findAllItemsByKey().apply(userLogin);

            if (clientProfile != null){

                results.add(
                        BuildClientsPortfolioRequest.builder()
                                                        .clientsLogin(clientProfile.getClientLogin())
                                                        .userId(clientProfile.getClientId())
                                                        .originalRequest(request)
                                                    .build()
                );

                logger.info(String.format(userExistsMsgTemplate, unitName, userLogin));

            } else {

                results.add(
                        FluxRoutedEnvelope.builder()
                                                .routeToAdapter("user_catalog_call")
                                                .requestObj(
                                                        FindUserInCatalogRequest.builder()
                                                                    .userLogin(request.getClientLogin())
                                                                    .originalRequest(request)
                                                                .build()
                                                )
                                            .build()
                );

                logger.info(String.format(requestUserFromCatalogMsgTemplate,unitName,userLogin));

            }


            return results;
        };

    }

}
