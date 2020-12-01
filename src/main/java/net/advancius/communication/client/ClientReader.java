package net.advancius.communication.client;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLogger;
import net.advancius.communication.CommunicationPacket;

import java.io.EOFException;
import java.io.IOException;

@Data
public class ClientReader extends Thread {

    private final Client client;

    @Override
    public void run() {
        while (true) {
            try {
                if (client.getSocket().isClosed()) {
                    AdvanciusLogger.info("Client was closed.");
                    client.disconnect();
                    break;
                }
                CommunicationPacket communicationPacket = client.getConnection().readPacket();
                AdvanciusBungee.getInstance().getCommunicationManager().handleReadPacket(client, communicationPacket);
            } catch (EOFException exception) {
                client.disconnect();
                break;
            } catch (IOException exception) {}
        }
    }
}
