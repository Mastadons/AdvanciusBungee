package net.advancius.listener.communication;

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

import java.util.UUID;

@FlagManager.FlaggedClass
public class ClientDumpRequest implements CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ClientDumpRequest());
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
}