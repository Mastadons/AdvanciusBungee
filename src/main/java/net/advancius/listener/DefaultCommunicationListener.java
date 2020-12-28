package net.advancius.listener;

import com.google.gson.JsonObject;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationManager;
import net.advancius.communication.CommunicationPacket;
import net.advancius.communication.client.Client;
import net.advancius.communication.client.ClientCredentials;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.MetadataContext;
import net.advancius.person.context.PermissionContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.protocol.Protocol;
import net.advancius.statistic.Statistic;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.UUID;

@FlagManager.FlaggedClass
public class DefaultCommunicationListener implements CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new DefaultCommunicationListener());
    }

    @CommunicationHandler(code = Protocol.CLIENT_CROSS_COMMAND)
    public void onCrossServerCommand(Client client, CommunicationPacket communicationPacket) {
        String serverName = communicationPacket.getMetadata().getMetadata("server");
        String command = communicationPacket.getMetadata().getMetadata("command");
        String sender = communicationPacket.getMetadata().getMetadata("sender");

        if (serverName.equalsIgnoreCase("Bungee")) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
            return;
        }
        ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);

        CommunicationPacket communicationResponse = CommunicationPacket.generatePacket(Protocol.SERVER_CROSS_COMMAND);
        communicationResponse.getMetadata().setMetadata("command", command);

        Client recipient = AdvanciusBungee.getInstance().getCommunicationManager().getClientFromServer(server);
        recipient.sendPacket(communicationResponse);

        PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().crossServer);
        placeholderComponent.replace("client", client);
        placeholderComponent.replace("sender", sender);
        placeholderComponent.replace("recipient", recipient);
        placeholderComponent.replace("command", command);
        placeholderComponent.translateColor();

        AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons(person -> PermissionContext.hasPermission(person, "advancius.crosscommand"))
                .forEach(placeholderComponent::send);
    }

    @CommunicationHandler(code = Protocol.CLIENT_DUMP_REQUEST)
    public void onClientDumpRequest(Client client, CommunicationPacket communicationPacket) {
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(UUID.fromString(communicationPacket.getMetadata().getMetadata("person")));
        String  path = communicationPacket.getMetadata().getMetadata("path");
        boolean json = communicationPacket.getMetadata().hasMetadata("json", true);

        PlaceholderComponent placeholderComponent = new PlaceholderComponent("{person." + path + '}');
        placeholderComponent.setReplaceJson(json);
        placeholderComponent.replace("person", person);

        CommunicationPacket communicationResponse = CommunicationPacket.generatePacket(Protocol.SERVER_DUMP_RESPONSE);
        communicationResponse.setRespondingTo(communicationPacket.getId());
        communicationResponse.getMetadata().setMetadata("dump", placeholderComponent.getText());

        client.sendPacket(communicationResponse);
    }

    @CommunicationHandler(code = Protocol.CLIENT_STATISTIC)
    public void onClientStatistic(Client client, CommunicationPacket communicationPacket) {
        UUID id = UUID.fromString(communicationPacket.getMetadata().getMetadata("id"));
        String namespace = communicationPacket.getMetadata().getMetadata("namespace");
        String name = communicationPacket.getMetadata().getMetadata("name");

        Statistic statistic = AdvanciusBungee.getInstance().getStatisticManager().getStatistic(namespace, name);
        Object score = null;
        if (statistic != null && statistic.hasScore(id))
            score = statistic.getScore(id).getScore(statistic.getStatisticClass());

        CommunicationPacket communicationResponse = CommunicationPacket.generatePacket(Protocol.SERVER_STATISTIC);
        communicationResponse.setRespondingTo(communicationPacket.getId());
        communicationResponse.getMetadata().setMetadata("score", score);

        client.sendPacket(communicationResponse);
    }

    @CommunicationHandler(code = Protocol.CLIENT_CREDENTIALS)
    public void onClientName(Client client, CommunicationPacket communicationPacket) {
        CommunicationManager communicationManager = AdvanciusBungee.getInstance().getCommunicationManager();

        String key = communicationPacket.getMetadata().getMetadata("key");
        ClientCredentials credentials = communicationManager.getCredentials(key);
        if (credentials == null) return;
        client.setCredentials(credentials);
    }

    @CommunicationHandler(code = Protocol.CLIENT_REPLACE_PERSISTENT_METADATA)
    public void onClientReplacePersistentMetadata(Client client, CommunicationPacket communicationPacket) {
        UUID personId = UUID.fromString(communicationPacket.getMetadata().getMetadata("person"));
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(personId);

        JsonObject replacement = communicationPacket.getMetadata().getMetadata("replacement");
        MetadataContext.getPersistentMetadata(person).deserialize(replacement);
    }

    @CommunicationHandler(code = Protocol.CLIENT_REPLACE_TRANSIENT_METADATA)
    public void onClientReplaceTransientMetadata(Client client, CommunicationPacket communicationPacket) {
        UUID personId = UUID.fromString(communicationPacket.getMetadata().getMetadata("person"));
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(personId);

        JsonObject replacement = communicationPacket.getMetadata().getMetadata("replacement");
        MetadataContext.getTransientMetadata(person).deserialize(replacement);
    }
}