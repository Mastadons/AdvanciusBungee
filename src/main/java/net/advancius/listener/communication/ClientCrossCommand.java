package net.advancius.listener.communication;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationPacket;
import net.advancius.communication.client.Client;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.context.PermissionContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.protocol.Protocol;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

@FlagManager.FlaggedClass
public class ClientCrossCommand implements CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ClientCrossCommand());
    }

    @CommunicationHandler(code = Protocol.CLIENT_CROSS_COMMAND)
    public void onCrossServerCommand(Client client, CommunicationPacket communicationPacket) {
        String serverName = communicationPacket.getMetadata().getMetadata("server");
        String command = communicationPacket.getMetadata().getMetadata("command");
        String sender = communicationPacket.getMetadata().getMetadata("sender");

        if (serverName.equalsIgnoreCase("Bungee")) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
            return;
        }
        ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);

        CommunicationPacket communicationResponse = CommunicationPacket.generatePacket(Protocol.SERVER_CROSS_COMMAND);
        communicationResponse.getMetadata().setMetadata("command", command);

        Client recipient = AdvanciusBungee.getInstance().getCommunicationManager().getClientFromServer(server);
        recipient.sendPacket(communicationResponse);

        PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().crossServer);
        placeholderComponent.replace("client", client);
        placeholderComponent.replace("sender", sender);
        placeholderComponent.replace("recipient", recipient);
        placeholderComponent.replace("command", command);
        placeholderComponent.translateColor();

        AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons(person -> PermissionContext.hasPermission(person, "advancius.crosscommand"))
                .forEach(placeholderComponent::send);
    }
}