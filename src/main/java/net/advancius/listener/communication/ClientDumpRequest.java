package net.advancius.listener.communication;

import net.advancius.AdvanciusBungee;
import net.advancius.communication.identifier.Identifier;
import net.advancius.communication.packet.PacketHandler;
import net.advancius.communication.packet.PacketListener;
import net.advancius.communication.packet.Packet;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.placeholder.PlaceholderComponent;

import java.util.UUID;

@FlagManager.FlaggedClass
public class ClientDumpRequest implements PacketListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ClientDumpRequest());
    }

    @PacketHandler(packetType = "dump_request")
    public void onClientDumpRequest(Identifier clientIdentifier, Packet packet) {
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(UUID.fromString(packet.getMetadata().getMetadata("person")));
        String path = packet.getMetadata().getMetadata("path");
        boolean json = packet.getMetadata().hasMetadata("json", true);

        PlaceholderComponent placeholderComponent = new PlaceholderComponent("{person." + path + '}');
        placeholderComponent.setReplaceJson(json);
        placeholderComponent.replace("person", person);

        packet.getResponseMetadata().setMetadata("dump", placeholderComponent.getText());
    }
}