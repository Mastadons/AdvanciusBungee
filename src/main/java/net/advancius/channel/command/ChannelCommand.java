package net.advancius.channel.command;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.channel.ChannelConfiguration;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.channel.message.ChannelMessage;
import net.advancius.command.CommandDescription;
import net.advancius.command.ExternalCommand;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.ChannelContext;
import net.advancius.person.context.ConnectionContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.placeholder.PlaceholderComponentBuilder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

@FlagManager.FlaggedClass
public class ChannelCommand extends ExternalCommand {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.POST_CHANNELS_LOAD)
    public static void loadChannelCommands() {
        for (ConfiguredChannel configuredChannel : ChannelConfiguration.getInstance().getConfiguredChannels()) {
            if (!configuredChannel.getMetadata().hasMetadata("command", true)) continue;

            List<String> aliases = configuredChannel.getMetadata().hasMetadata("commandAliases") ? configuredChannel.getMetadata().getMetadata("commandAliases") : new ArrayList<>();

            CommandDescription description = new CommandDescription(configuredChannel.getName(), configuredChannel.getGuard().getSendPermission(), "", aliases);
            AdvanciusBungee.getInstance().getCommandManager().registerExternalCommand(new ChannelCommand(configuredChannel, description));
        }
    }

    private final ConfiguredChannel channel;

    public ChannelCommand(ConfiguredChannel channel, CommandDescription commandDescription) {
        super(commandDescription);
        this.channel = channel;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (!(sender instanceof ProxiedPlayer)) return;

        Person person = AdvanciusBungee.getInstance().getPersonManager().getPersonUnsafe(sender);

        ChannelContext channelContext = person.getContextManager().getContext(ChannelContext.class);
        ConnectionContext connectionContext = person.getContextManager().getContext(ConnectionContext.class);

        if (arguments.length == 0) {
            if (!channel.getMetadata().getMetadataOr("switchable", true)) {
                PlaceholderComponentBuilder.create(AdvanciusLang.getInstance().incorrectSyntax)
                        .replace("syntax", "/<channel> <message>").sendColored(person);
                return;
            }
            if (channelContext.getChannel().equals(channel)) {
                PlaceholderComponentBuilder.create(AdvanciusLang.getInstance().channelAlreadyIn)
                        .replace("channel", channel).sendColored(person);
                return;
            }
            channelContext.setChannel(channel);
            PlaceholderComponentBuilder.create(AdvanciusLang.getInstance().channelChange)
                    .replace("channel", channel).sendColored(person);
            return;
        }
        String message = String.join(" ", arguments);
        List<ChannelMessage> messageList = AdvanciusBungee.getInstance().getChannelManager().generateMessage(person, channel, message, AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons());
        messageList.forEach(ChannelMessage::send);
    }
}