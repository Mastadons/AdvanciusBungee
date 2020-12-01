package net.advancius.communication.client;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLogger;
import net.advancius.communication.CommunicationPacket;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;

@Data
public class Client {

    private String name;
    private final Socket socket;
    private final ClientReader clientReader = new ClientReader(this);
    private final ClientConnection connection;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.connection = new ClientConnection(socket);
    }

    public boolean sendPacket(CommunicationPacket communicationPacket) {
        try {
            connection.sendPacket(communicationPacket);
            AdvanciusLogger.log(Level.INFO, "[Network] Successfully sent packet(%s) with code %d",
                    communicationPacket.getId().toString(), communicationPacket.getCode());
            return true;
        } catch (IOException exception) {
            AdvanciusLogger.log(Level.INFO, "[Network] Failed to send packet(%s) with code %d",
                    communicationPacket.getId().toString(), communicationPacket.getCode());
            return false;
        }
    }
    public void disconnect() {
        try {
            AdvanciusBungee.getInstance().getCommunicationManager().unregisterClient(this);
            clientReader.stop();
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
