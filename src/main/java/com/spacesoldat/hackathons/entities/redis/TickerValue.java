package com.spacesoldat.hackathons.entities.redis;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TickerValue {
    private String tickerName;  //  obviously a ticker name, you know
    private String price;       //  a string representation of a floating point number having 4 digits after the point
}
