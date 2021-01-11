package net.advancius.communication.packet;

import lombok.Data;
import lombok.Getter;
import net.advancius.utils.Metadata;

import java.util.UUID;

@Data
public class Packet {

    private final String type;
    private final UUID id;
    private final long timestamp;

    private final Metadata metadata;
    private Metadata responseMetadata;

    public static Packet generatePacket(String type) {
        return new Packet(type, UUID.randomUUID(), System.currentTimeMillis(), new Metadata());
    }
}
