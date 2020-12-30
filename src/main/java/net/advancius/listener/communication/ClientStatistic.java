package net.advancius.listener.communication;

import net.advancius.AdvanciusBungee;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationPacket;
import net.advancius.communication.client.Client;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.protocol.Protocol;
import net.advancius.statistic.Statistic;

import java.util.UUID;

@FlagManager.FlaggedClass
public class ClientStatistic implements CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ClientStatistic());
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
}