package net.advancius.channel;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.AdvanciusLogger;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.channel.message.ChannelMessage;
import net.advancius.channel.message.event.MessageGenerateEvent;
import net.advancius.communication.CommunicationHandler;
import net.advancius.communication.CommunicationListener;
import net.advancius.communication.CommunicationPacket;
import net.advancius.communication.client.Client;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.ConnectionContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.person.context.PermissionContext;
import net.advancius.protocol.Protocol;
import net.advancius.utils.ColorUtils;
import net.advancius.utils.Metadata;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@FlagManager.FlaggedClass
public class ChannelManager implements Listener, CommunicationListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    private static void channelManager() {
        ChannelManager instance = new ChannelManager();
        AdvanciusBungee.getInstance().setChannelManager(instance);

        ProxyServer.getInstance().getPluginManager().registerListener(AdvanciusBungee.getInstance(), instance);
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(instance);

        FlagManager.runFlaggedMethods("net.advancius", DefinedFlag.POST_CHANNELS_LOAD);
    }

    @EventHandler(priority = 100)
    public void onPersonChat(ChatEvent event) {
        if (event.isCommand() || !(event.getSender() instanceof ProxiedPlayer)) return;

        if (!(event.getReceiver() instanceof Server)) return;
        Server server = (Server) event.getReceiver();
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(((ProxiedPlayer) event.getSender()).getUniqueId());
        String message = event.getMessage();

        if (isServerProcessing(server.getInfo())) return;
        event.setCancelled(true);

        handleNaturalChat(person, message);
    }

    @CommunicationHandler(code = Protocol.CLIENT_CHAT)
    public void onServerChat(Client client, CommunicationPacket communicationPacket) {
        UUID personId = UUID.fromString(communicationPacket.getMetadata().getMetadata("person"));

        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(personId);
        String message = communicationPacket.getMetadata().getMetadata("message");

        handleNaturalChat(person, message);
    }

    private void handleNaturalChat(Person person, String message) {
        ConnectionContext connection = person.getContextManager().getContext(ConnectionContext.class);
        PermissionContext permission = person.getContextManager().getContext(PermissionContext.class);

        ConfiguredChannel shortcutChannel = getShortcutChannel(person, message);
        ConfiguredChannel channel = getChannel(person);
        if (shortcutChannel != null) {
            channel = shortcutChannel;
            message = message.substring(1);
        }
        if (channel.getMetadata().hasMetadata("locked", true) && !permission.hasPermission(AdvanciusBungee.getInstance().getCommandManager().getDescription("chatlock").getPermission())) {
            connection.sendMessage(ColorUtils.toTextComponent(AdvanciusLang.getInstance().cannotChatLocked));
            return;
        }

        List<ChannelMessage> messageList = generateMessage(person, channel, message, AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons());

        AdvanciusLogger.info(String.format("[Chat] (%s) %s: %s", channel.getName(), connection.getConnectionName(), message));
        messageList.forEach(ChannelMessage::send);
    }

    public List<ChannelMessage> generateMessage(Person sender, String message) {
        return generateMessage(sender, getChannel(sender), message, AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons());
    }

    public synchronized List<ChannelMessage> generateMessage(Person sender, Channel channel, String message, List<Person> readers) {
        MessageGenerateEvent messageGenerateEvent = AdvanciusBungee.getInstance().getEventManager().generateEvent(MessageGenerateEvent.class, this, sender, channel, message, readers);
        AdvanciusBungee.getInstance().getEventManager().executeEvent(messageGenerateEvent);

        if (messageGenerateEvent.isCancelled()) return new ArrayList<>();
        return messageGenerateEvent.generate();
    }

    private ConfiguredChannel getShortcutChannel(Person sender, String message) {
        if (message.length() < 2) return null;
        for (ConfiguredChannel configuredChannel : ChannelConfiguration.getInstance().getConfiguredChannels()) {
            if (!configuredChannel.getGuard().canPersonSend(sender)) continue;
            if (!configuredChannel.getMetadata().hasMetadata("shortcut")) continue;
            String shortcut = configuredChannel.getMetadata().getMetadata("shortcut");

            if (message.startsWith(shortcut)) return configuredChannel;
        }
        return null;
    }

    public ConfiguredChannel getChannel(Person person) {
        Metadata persistentMetadata = MetadataContext.getPersistentMetadata(person);

        if (persistentMetadata.hasMetadata("channel")) {
            ConfiguredChannel channel = getChannel(persistentMetadata.getMetadata("channel").toString());
            if (channel == null) {
                persistentMetadata.setMetadata("channel", getDefaultChannel().getName());
                return getDefaultChannel();
            }
            return channel;
        }
        return getDefaultChannel();
    }

    public ConfiguredChannel getChannel(String name) {
        return ChannelConfiguration.getInstance().getConfiguredChannels().stream().filter(channel -> channel.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public ConfiguredChannel getDefaultChannel() {
        return getChannel(ChannelConfiguration.getInstance().getDefaultChannel());
    }

    public boolean isServerProcessing(ServerInfo server) {
        return AdvanciusConfiguration.getInstance().getServerProcessing().contains(server.getName());
    }
}