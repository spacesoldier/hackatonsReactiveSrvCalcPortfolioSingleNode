package com.spacesoldat.hackathons.entities.internal.queries;

import com.spacesoldat.hackathons.entities.io.ClientPortfolioRequest;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class NoUserFoundInCatalogResponse {
    private String errorDesc;
    private ClientPortfolioRequest originalRequest;
}
