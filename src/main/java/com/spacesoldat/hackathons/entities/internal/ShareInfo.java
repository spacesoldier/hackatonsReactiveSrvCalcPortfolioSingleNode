package com.spacesoldat.hackathons.entities.internal;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ShareInfo {
    private String ticker;          //  stock ticker name
    private int balance;            //  shares quantity in portfolio
    private double totalCost;       //  total cost of shares for this ticker according to the latest data
    private String operationId;     //  an id of the last message about the balance update for this share type in portfolio
}
