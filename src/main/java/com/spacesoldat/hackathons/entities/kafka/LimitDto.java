package com.spacesoldat.hackathons.entities.kafka;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class LimitDto {
    private String ticker;  //  stock ticker name
    private int balance;    //  shares quantity in portfolio
}
