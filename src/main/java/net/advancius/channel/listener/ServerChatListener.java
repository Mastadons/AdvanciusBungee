package net.advancius.channel.listener;

import net.advancius.AdvanciusBungee;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationPacket;
import net.advancius.communication.client.Client;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.protocol.Protocol;

import java.util.UUID;

@FlagManager.FlaggedClass
public class ServerChatListener implements CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    private static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ServerChatListener());
    }

    @CommunicationHandler(code = Protocol.CLIENT_CHAT)
    public void onServerChat(Client client, CommunicationPacket communicationPacket) {
        UUID personId = UUID.fromString(communicationPacket.getMetadata().getMetadata("person"));

        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(personId);
        String message = communicationPacket.getMetadata().getMetadata("message");

        AdvanciusBungee.getInstance().getChannelManager().handleNaturalChat(person, message);
    }
}
