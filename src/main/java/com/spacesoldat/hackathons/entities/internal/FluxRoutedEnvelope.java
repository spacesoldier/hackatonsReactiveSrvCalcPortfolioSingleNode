package com.spacesoldat.hackathons.entities.internal;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class FluxRoutedEnvelope {
    private String requestKey;
    private Object requestObj;
    private String routeToAdapter;
}
