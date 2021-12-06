package com.spacesoldat.hackathons.entities.internal.queries;

import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class FindUserInCatalogRequest {
    private String userLogin;                           // user's login to query from catalog
    private ClientPortfolioRequest originalRequest;     // for aggregation and loopback after service call
}
