package com.spacesoldat.hackathons.entities.internal.queries;

import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class BuildClientsPortfolioRequest {
    private String userId;
    private String clientsLogin;
    private ClientPortfolioRequest originalRequest;     // for aggregation purposes
}
