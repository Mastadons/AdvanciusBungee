package net.advancius.listener.communication;

import com.google.gson.JsonObject;
import net.advancius.AdvanciusBungee;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationPacket;
import net.advancius.communication.client.Client;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.MetadataContext;
import net.advancius.protocol.Protocol;

import java.util.UUID;

@FlagManager.FlaggedClass
public class ClientReplaceMetadata implements CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ClientReplaceMetadata());
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