package net.advancius.communication.packet;

import lombok.Data;
import net.advancius.communication.identifier.Identifier;
import net.advancius.utils.Reflection;

import java.lang.reflect.Method;

@Data
public class PacketHandlerMethod {

    private final PacketListener listener;
    private final Method method;

    public void executeMethod(Identifier identifier, Packet packet) {
        if (!isMatchingPacket(packet)) return;

        Reflection.runMethod(method, listener, identifier, packet);
    }

    private boolean isMatchingPacket(Packet packet) {
        return method.getParameterTypes()[1].isAssignableFrom(packet.getClass());
    }
}
