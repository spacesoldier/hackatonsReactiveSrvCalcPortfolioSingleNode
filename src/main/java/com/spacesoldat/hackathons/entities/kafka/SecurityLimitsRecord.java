package com.spacesoldat.hackathons.entities.kafka;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class SecurityLimitsRecord {
    private String operationId;             //  message UUID
    private String clientId;                //  client ID
    private String type;                    //  could be SNAPSHOT, INCREMENT or REPORT
    private List<LimitDto> limits;          //  a list of LimitDto
    private String timestamp;               //  time when the message was sent, format “2021-11-26T13:12:18.463212Z”
}
