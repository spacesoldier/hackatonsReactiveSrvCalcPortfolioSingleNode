package com.spacesoldat.hackathons.web;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class WebCallParam {
    private String name;
    private String value;
}
