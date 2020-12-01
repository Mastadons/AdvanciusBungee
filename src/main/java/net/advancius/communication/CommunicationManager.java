package net.advancius.communication;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLogger;
import net.advancius.communication.client.Client;
import net.advancius.communication.client.ClientConnector;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@Data
@FlagManager.FlaggedClass
public class CommunicationManager {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 5)
    private static void loadChannelManager() throws IOException {
        AdvanciusLogger.info("Loading ChannelManager");
        CommunicationManager instance = new CommunicationManager();
        instance.startCommunication();

        AdvanciusBungee.getInstance().setCommunicationManager(instance);
    }

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_SAVE, priority = 100)
    private static void saveChannelManager() throws IOException, InterruptedException {
        AdvanciusLogger.info("Saving ChannelManager");
        CommunicationManager instance = AdvanciusBungee.getInstance().getCommunicationManager();
        instance.stopCommunication();
    }

    private final Map<UUID, AtomicReference<CommunicationPacket>> requestMap = new HashMap<>();
    private final List<CommunicationListener> listenerList = new ArrayList<>();

    private final List<UUID> usedIds = Collections.synchronizedList(new ArrayList<>());

    private ServerSocket serverSocket;
    private ClientConnector clientConnector;

    private final List<Client> clientList = new ArrayList<>();

    public void startCommunication() throws IOException {
        serverSocket = new ServerSocket(AdvanciusConfiguration.getInstance().port);

        AdvanciusLogger.info("Started communication server");

        clientConnector = new ClientConnector(this);
        clientConnector.start();
        AdvanciusLogger.info("Started client connector");
    }

    public void stopCommunication() throws IOException {
        clientConnector.stop();

        new ArrayList<>(clientList).forEach(Client::disconnect);
        clientList.clear();
        AdvanciusLogger.info("Disconnected all clients");

        serverSocket.close();
        AdvanciusLogger.info("Stopped communication server");
    }

    public void handleReadPacket(Client client, CommunicationPacket communicationPacket) {
        if (packetExists(communicationPacket)) return;
        if (handleRequest(communicationPacket)) return;

        AdvanciusLogger.log(Level.INFO, "[Network] (%s) Incoming packet(%s) with code %d",
                client.getName(), communicationPacket.getId().toString(), communicationPacket.getCode());

        listenerList.forEach(listener -> listener.getListenerMethods(communicationPacket.getCode()).forEach(method -> method.executeMethod(client, communicationPacket)));
    }

    private boolean packetExists(CommunicationPacket communicationPacket) {
        if (usedIds.contains(communicationPacket.getId())) return true;
        usedIds.add(communicationPacket.getId());
        return false;
    }

    public Client getClient(ServerInfo serverInfo) {
        for (Client client : clientList)
            if (client.getName() != null && client.getName().equalsIgnoreCase(serverInfo.getName())) return client;
        return null;
    }

    private boolean handleRequest(CommunicationPacket communicationPacket) {
        if (communicationPacket.getRespondingTo() == null) return false;
        requestMap.forEach((requestId, reference) -> {
            if (!communicationPacket.getRespondingTo().equals(requestId)) return;
            AdvanciusLogger.log(Level.INFO, "[Network] Handling incoming response(%s) with code %d",
                    communicationPacket.getId().toString(), communicationPacket.getCode());
            reference.set(communicationPacket);
        });
        return requestMap.remove(communicationPacket.getRespondingTo()) != null;
    }

    public void awaitResponse(CommunicationPacket communicationPacket, AtomicReference<CommunicationPacket> reference) {
        requestMap.put(communicationPacket.getId(), reference);
    }

    public void registerListener(CommunicationListener listener) {
        listenerList.add(listener);
    }

    public void unregisterListener(CommunicationListener listener) {
        listenerList.remove(listener);
    }

    public void registerClient(Client client) {
        clientList.add(client);
    }

    public void unregisterClient(Client client) { clientList.remove(client); }
}
