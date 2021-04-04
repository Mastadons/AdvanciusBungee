package net.advancius.listener.communication;

import lombok.AllArgsConstructor;
import lombok.Data;
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
import java.util.List;

@FlagManager.FlaggedClass
public class ClientCrossCommand implements PacketListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ClientCrossCommand());
    }

    @PacketHandler(packetType = "cross_command")
    public void onCrossServerCommand(Identifier identifier, Packet packet) throws IOException {
        String serverName = packet.getMetadata().getMetadata("server");
        String command = packet.getMetadata().getMetadata("command");

        if (serverName.equalsIgnoreCase("Bungee")) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
            return;
        }
        ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);

        Packet redirect = Packet.generatePacket("execute_command");
        redirect.getMetadata().setMetadata("command", command);

        Session session = AdvanciusBungee.getInstance().getCommunicationManager().getSessionManager().getSession(server);
        session.sendPacket(redirect);
    }
}
/*
/csc silent=true
 */

@Data
@AllArgsConstructor
class CrossCommandMetadata {

    private List<String> servers;
    private String command;
    private boolean silent;
}