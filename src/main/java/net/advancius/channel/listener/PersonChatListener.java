package net.advancius.channel.listener;

import net.advancius.AdvanciusBungee;
import net.advancius.channel.ChannelManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@FlagManager.FlaggedClass
public class PersonChatListener implements Listener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    private static void load() {
        ProxyServer.getInstance().getPluginManager().registerListener(AdvanciusBungee.getInstance(), new PersonChatListener());
    }

    @EventHandler(priority = 100)
    public void onPersonChat(ChatEvent event) {
        ChannelManager channelManager = AdvanciusBungee.getInstance().getChannelManager();

        if (event.isCommand() || !(event.getSender() instanceof ProxiedPlayer)) return;

        if (!(event.getReceiver() instanceof Server)) return;
        Server server = (Server) event.getReceiver();
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(((ProxiedPlayer) event.getSender()).getUniqueId());
        String message = event.getMessage();

        if (channelManager.isServerProcessing(server.getInfo())) return;
        event.setCancelled(true);

        channelManager.handleNaturalChat(person, message);
    }
}