package net.advancius.listener;

import net.advancius.AdvanciusBungee;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationPacket;
import net.advancius.communication.client.Client;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
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
        ServerInfo server = ProxyServer.getInstance().getServerInfo(communicationPacket.getMetadata().getMetadata("server"));

        CommunicationPacket communicationResponse = CommunicationPacket.generatePacket(Protocol.SERVER_CROSS_COMMAND);
        communicationResponse.getMetadata().setMetadata("command", communicationPacket.getMetadata().getMetadata("command"));

        Client recipient = AdvanciusBungee.getInstance().getCommunicationManager().getClient(server);
        recipient.sendPacket(communicationResponse);
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

    @CommunicationHandler(code = Protocol.CLIENT_NAME)
    public void onClientName(Client client, CommunicationPacket communicationPacket) {
        client.setName(communicationPacket.getMetadata().getMetadata("name"));
    }
}
