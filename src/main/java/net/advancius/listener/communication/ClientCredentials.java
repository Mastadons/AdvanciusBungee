package net.advancius.listener.communication;

import net.advancius.AdvanciusBungee;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationManager;
import net.advancius.communication.CommunicationPacket;
import net.advancius.communication.client.Client;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.protocol.Protocol;

@FlagManager.FlaggedClass
public class ClientCredentials implements CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ClientCredentials());
    }

    @CommunicationHandler(code = Protocol.CLIENT_CREDENTIALS)
    public void onClientCredentials(Client client, CommunicationPacket communicationPacket) {
        CommunicationManager communicationManager = AdvanciusBungee.getInstance().getCommunicationManager();

        String key = communicationPacket.getMetadata().getMetadata("key");
        net.advancius.communication.client.ClientCredentials credentials = communicationManager.getCredentials(key);
        if (credentials == null) return;
        client.setCredentials(credentials);
    }
}