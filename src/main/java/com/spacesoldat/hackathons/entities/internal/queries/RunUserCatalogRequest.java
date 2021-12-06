package com.spacesoldat.hackathons.entities.internal.queries;

import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;

@Data @Builder
public class RunUserCatalogRequest {
    private WebClient.RequestBodySpec requestBodySpec;
    private ClientPortfolioRequest originalRequest;         // for aggregation purposes
}
