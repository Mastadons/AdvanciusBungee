package net.advancius.communication.client;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLogger;
import net.advancius.communication.CommunicationManager;
import net.advancius.communication.CommunicationPacket;
import net.advancius.protocol.Protocol;

import java.io.EOFException;
import java.io.IOException;

@Data
public class ClientReader extends Thread {

    private final Client client;

    @Override
    public void run() {
        CommunicationManager communicationManager = AdvanciusBungee.getInstance().getCommunicationManager();

        CommunicationPacket encryptionPacket = CommunicationPacket.generatePacket(Protocol.SERVER_ASYMMETRIC_ENCRYPTION);
        encryptionPacket.getMetadata().setMetadata("public_key", communicationManager.getEncryptionKeypair().getPublicBase64());
        client.sendPacket(encryptionPacket);

        while (true) {
            try {
                if (client.getSocket().isClosed()) {
                    AdvanciusLogger.info("Client was closed.");
                    client.disconnect();
                    break;
                }
                CommunicationPacket communicationPacket = client.getConnection().readPacket(client);

                if (client.getEncryptionKey() == null && communicationPacket.getCode() != Protocol.CLIENT_ENCRYPTION) {
                    AdvanciusLogger.warn("Client attempted to send unencrypted data, disconnecting.");
                    client.disconnect();
                    break;
                }

                if (client.getEncryptionKey() != null && client.getCredentials() == null && communicationPacket.getCode() != Protocol.CLIENT_CREDENTIALS) {
                    AdvanciusLogger.warn("Unauthenticated client attempted to send data, disconnecting.");
                    client.disconnect();
                    break;
                }

                AdvanciusBungee.getInstance().getCommunicationManager().handleReadPacket(client, communicationPacket);
            } catch (EOFException exception) {
                client.disconnect();
                break;
            } catch (IOException exception) {}
        }
    }
}
