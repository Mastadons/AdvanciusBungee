package net.advancius.communication.client;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.communication.CommunicationPacket;
import net.advancius.encryption.AsymmetricEncryption;
import net.advancius.encryption.SymmetricEncryption;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
public class ClientConnection {

    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public ClientConnection(Socket socket) throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public CommunicationPacket readPacket(Client client) throws IOException {
        String data = inputStream.readUTF();
        if (client.getEncryptionKey() != null) {
            byte[] encryptedData = Base64.getDecoder().decode(data);
            String decryptedData0 = SymmetricEncryption.decrypt(encryptedData, client.getEncryptionKey(), client.getSalt());
            return AdvanciusBungee.GSON.fromJson(decryptedData0, CommunicationPacket.class);
        }
        return AdvanciusBungee.GSON.fromJson(data, CommunicationPacket.class);
    }

    public void sendPacket(Client client, CommunicationPacket communicationPacket) throws IOException {
        String data = AdvanciusBungee.GSON.toJson(communicationPacket);
        if (client.getEncryptionKey() != null) {
            byte[] encryptedData = SymmetricEncryption.encrypt(data, client.getEncryptionKey(), client.getSalt());
            String encryptedData0 = Base64.getEncoder().encodeToString(encryptedData);
            outputStream.writeUTF(encryptedData0);
            return;
        }
        outputStream.writeUTF(data);
    }
}
