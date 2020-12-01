package net.advancius.communication.client;

import lombok.Data;
import net.advancius.AdvanciusLogger;
import net.advancius.communication.CommunicationManager;

import java.io.IOException;
import java.net.Socket;

@Data
public class ClientConnector extends Thread {

    private final CommunicationManager communicationManager;

    @Override
    public void run() {
        while (!communicationManager.getServerSocket().isClosed()) {
            try {
                AdvanciusLogger.info("Waiting for a client to connect...");
                Socket socket = communicationManager.getServerSocket().accept();
                Client client = new Client(socket);

                communicationManager.registerClient(client);
                client.getClientReader().start();
                AdvanciusLogger.info("Unnamed client has connected.");
            } catch (IOException exception) {
                if (!communicationManager.getServerSocket().isClosed())
                    AdvanciusLogger.error("Encountered exception connecting client. ", exception);
            }
        }
    }
}
