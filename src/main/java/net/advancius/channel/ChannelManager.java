package net.advancius.channel;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.AdvanciusLogger;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.channel.message.ChannelMessage;
import net.advancius.channel.message.event.MessageGenerateEvent;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.ChannelContext;
import net.advancius.person.context.ConnectionContext;
import net.advancius.person.context.PermissionContext;
import net.advancius.utils.ColorUtils;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;

import java.util.ArrayList;
import java.util.List;

@FlagManager.FlaggedClass
public class ChannelManager implements Listener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    private static void channelManager() {
        ChannelManager instance = new ChannelManager();
        AdvanciusBungee.getInstance().setChannelManager(instance);

        FlagManager.runFlaggedMethods("net.advancius", DefinedFlag.POST_CHANNELS_LOAD);
    }

    public void handleNaturalChat(Person person, String message) {
        ConnectionContext connection = person.getContextManager().getContext(ConnectionContext.class);

        ConfiguredChannel shortcutChannel = getShortcutChannel(person, message);
        ConfiguredChannel channel = getChannel(person);
        if (shortcutChannel != null) {
            channel = shortcutChannel;
            message = message.substring(1);
        }
        if (checkChannelLocked(person, channel)) return;

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

    public boolean checkChannelLocked(Person person, ConfiguredChannel channel) {
        if (channel.getMetadata().hasMetadata("locked", true) && !PermissionContext.hasPermission(person, AdvanciusBungee.getInstance().getCommandManager().getDescription("chatlock").getPermission())) {
            ConnectionContext.sendMessage(person, ColorUtils.toTextComponent(AdvanciusLang.getInstance().cannotChatLocked));
            return true;
        }
        return false;
    }

    public ConfiguredChannel getChannel(Person person) {
        return person.getContextManager().getContext(ChannelContext.class).getChannel();
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