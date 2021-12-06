package com.spacesoldat.hackathons.entities.io;

import lombok.Data;
import lombok.Builder;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

@Data
@Builder
public class ClientPortfolioRequest {
    private String rqId;
    private ServerWebExchange serverWebExchange;
    private String clientLogin;
}
