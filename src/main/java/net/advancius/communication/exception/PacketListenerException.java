package net.advancius.communication.exception;

import lombok.Getter;
import net.advancius.communication.packet.Packet;
import net.advancius.communication.packet.PacketHandlerMethod;
import net.advancius.communication.packet.PacketListener;

public class PacketListenerException extends Exception {

    @Getter private final PacketListener listener;
    @Getter private final PacketHandlerMethod handlerMethod;
    @Getter private final Packet packet;


    public PacketListenerException(PacketListener listener, PacketHandlerMethod handlerMethod, Packet packet, Throwable cause) {
        super(cause);
        this.packet = packet;
        this.listener = listener;
        this.handlerMethod = handlerMethod;
    }
}
