package net.advancius.communication.session;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.communication.identifier.Identifier;
import net.advancius.communication.packet.Packet;

import java.io.IOException;

@Data
public class Session {

    private final Identifier identifier;
    private final org.eclipse.jetty.websocket.api.Session session;

    public void sendPacket(Packet packet) throws IOException {
        String packetJson = AdvanciusBungee.GSON.toJson(packet);

        session.getRemote().sendString(packetJson);
    }
}
