package net.advancius.listener.communication;

import net.advancius.AdvanciusBungee;
import net.advancius.communication.identifier.Identifier;
import net.advancius.communication.packet.PacketHandler;
import net.advancius.communication.packet.PacketListener;
import net.advancius.communication.packet.Packet;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.statistic.Statistic;

import java.util.UUID;

@FlagManager.FlaggedClass
public class ClientStatistic implements PacketListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ClientStatistic());
    }

    @PacketHandler(packetType = "statistic")
    public void onClientStatistic(Identifier clientIdentifier, Packet packet) {
        UUID id = UUID.fromString(packet.getMetadata().getMetadata("id"));
        String namespace = packet.getMetadata().getMetadata("namespace");
        String name = packet.getMetadata().getMetadata("name");

        Statistic statistic = AdvanciusBungee.getInstance().getStatisticManager().getStatistic(namespace, name);
        Object score = null;
        if (statistic != null && statistic.hasScore(id))
            score = statistic.getScore(id).getScore(statistic.getStatisticClass());

        packet.getResponseMetadata().setMetadata("score", score);
    }
}