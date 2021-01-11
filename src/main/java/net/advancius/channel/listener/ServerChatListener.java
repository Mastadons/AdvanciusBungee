package net.advancius.channel.listener;

import net.advancius.AdvanciusBungee;
import net.advancius.communication.identifier.Identifier;
import net.advancius.communication.packet.PacketHandler;
import net.advancius.communication.packet.PacketListener;
import net.advancius.communication.packet.Packet;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;

import java.util.UUID;

@FlagManager.FlaggedClass
public class ServerChatListener implements PacketListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    private static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ServerChatListener());
    }

    @PacketHandler(packetType = "server_chat")
    public void onServerChat(Identifier clientIdentifier, Packet packet) {
        UUID personId = UUID.fromString(packet.getMetadata().getMetadata("person"));

        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(personId);
        String message = packet.getMetadata().getMetadata("message");

        AdvanciusBungee.getInstance().getChannelManager().handleNaturalChat(person, message);
    }
}
