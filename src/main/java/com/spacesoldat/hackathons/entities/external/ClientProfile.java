package com.spacesoldat.hackathons.entities.external;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ClientProfile {
    private String clientLogin;     //  client's login
    private String clientId;        //  UUID of the client's record
    private String name;            //  client's full name, up to 100 symbols
    private String address;         //  client's address, up to 500 symbols
}
