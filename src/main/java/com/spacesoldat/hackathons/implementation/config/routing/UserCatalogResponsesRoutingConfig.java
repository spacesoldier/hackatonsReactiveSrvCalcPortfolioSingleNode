package com.spacesoldat.hackathons.implementation.config.routing;

import com.spacesoldat.hackathons.entities.external.ClientProfile;
import com.spacesoldat.hackathons.entities.internal.queries.BuildClientsPortfolioRequest;
import com.spacesoldat.hackathons.entities.internal.queries.NoUserFoundInCatalogResponse;
import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import com.spacesoldat.hackathons.streaming.manage.FluxWiresManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.streams.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class UserCatalogResponsesRoutingConfig {

    @Autowired
    @Qualifier("FluxWiringManager")
    private FluxWiresManager fluxManager;

    @Bean(name="onFailCatalogRsHandler")
    public BiConsumer<KeyValue,Object> onFailConsumer(){
        return (kv, err) -> {

            NoUserFoundInCatalogResponse errResponse = NoUserFoundInCatalogResponse.builder()
                    .errorDesc(err.toString())
                    .originalRequest((ClientPortfolioRequest) kv.value)
                    .build();

            fluxManager.getSink("serve").accept(errResponse);

        };
    }

    @Bean(name="onSuccessCatalogRsHandler")
    public BiConsumer<KeyValue,Object> onSuccessConsumer(){

        Gson gson = new GsonBuilder().create();

        return (kv, userDataObj) -> {

            String userDataStr = (String) userDataObj;

            ClientProfile clientProfile = gson.fromJson(userDataStr, ClientProfile.class);

            fluxManager.getSink("updates").accept(clientProfile);

            BuildClientsPortfolioRequest okResponse = BuildClientsPortfolioRequest.builder()
                    .clientsLogin(clientProfile.getClientLogin())
                    .userId(clientProfile.getClientId())
                    .originalRequest((ClientPortfolioRequest) kv.value)
                    .build();

            fluxManager.getSink("serve").accept(okResponse);

        };
    }
}
