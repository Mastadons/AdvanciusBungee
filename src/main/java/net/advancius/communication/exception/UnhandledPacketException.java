package net.advancius.communication.exception;

import lombok.Getter;
import net.advancius.communication.packet.Packet;

public class UnhandledPacketException extends Exception {

    @Getter private final Packet packet;

    public UnhandledPacketException(Packet packet) {
        super("No listener setup to handle packets of type " + packet.getType());
        this.packet = packet;
    }
}
