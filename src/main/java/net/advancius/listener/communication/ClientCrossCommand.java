package net.advancius.listener.communication;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.communication.identifier.Identifier;
import net.advancius.communication.packet.PacketHandler;
import net.advancius.communication.packet.PacketListener;
import net.advancius.communication.packet.Packet;
import net.advancius.communication.session.Session;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.context.PermissionContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;

@FlagManager.FlaggedClass
public class ClientCrossCommand implements PacketListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ClientCrossCommand());
    }

    @PacketHandler(packetType = "cross_command")
    public void onCrossServerCommand(Identifier clientIdentifier, Packet packet) throws IOException {
        String serverName = packet.getMetadata().getMetadata("server");
        String command = packet.getMetadata().getMetadata("command");
        String sender = packet.getMetadata().getMetadata("sender");

        if (serverName.equalsIgnoreCase("Bungee")) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
            return;
        }
        ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);

        Packet redirect = Packet.generatePacket("execute_command");
        redirect.getMetadata().setMetadata("command", command);

        Session session = AdvanciusBungee.getInstance().getCommunicationManager().getSessionManager().getSession(server);
        session.sendPacket(redirect);

        PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().crossServer);
        placeholderComponent.replace("client", clientIdentifier);
        placeholderComponent.replace("sender", sender);
        placeholderComponent.replace("recipient", session.getIdentifier());
        placeholderComponent.replace("command", command);
        placeholderComponent.translateColor();

        AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons(person -> PermissionContext.hasPermission(person, "advancius.crosscommand"))
                .forEach(placeholderComponent::send);
    }
}