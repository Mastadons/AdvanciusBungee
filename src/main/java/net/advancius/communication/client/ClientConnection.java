package net.advancius.communication.client;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.communication.CommunicationPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Data
public class ClientConnection {

    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public ClientConnection(Socket socket) throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public CommunicationPacket readPacket() throws IOException {
        String data = inputStream.readUTF();
        return AdvanciusBungee.GSON.fromJson(data, CommunicationPacket.class);
    }

    public void sendPacket(CommunicationPacket communicationPacket) throws IOException {
        String data = AdvanciusBungee.GSON.toJson(communicationPacket);
        outputStream.writeUTF(data);
    }
}
