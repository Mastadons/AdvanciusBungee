package net.advancius.communication.client;

import lombok.Data;

@Data
public class ClientCredentials {

    private String key;
    private String name;
    private boolean internal;
}
