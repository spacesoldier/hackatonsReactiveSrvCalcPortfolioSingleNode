package com.spacesoldat.hackathons.entities.io;

import com.spacesoldat.hackathons.entities.internal.ShareInfo;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Data @Builder
public class ClientPortfolioResponse {
    private String requestId;                   // our internal correlation Id for incoming request
    private ServerWebExchange exchange;         // an aggregate to be able to construct the response
    private List<ShareInfo> sharesInPortfolio;
}
