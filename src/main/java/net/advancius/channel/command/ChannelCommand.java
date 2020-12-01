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
import net.advancius.person.context.BungeecordContext;
import net.advancius.person.context.ChannelContext;
import net.advancius.placeholder.PlaceholderComponent;
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

        ChannelContext channelContext = person.getContextManager().getContext("channel");
        BungeecordContext bungeecordContext = person.getContextManager().getContext("bungeecord");

        if (arguments.length == 0) {
            if (!channel.getMetadata().getMetadataOr("switchable", true)) {
                PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().incorrectSyntax);
                placeholderComponent.replace("syntax", "/<channel> <message>");
                placeholderComponent.translateColor();

                bungeecordContext.sendMessage(placeholderComponent.toTextComponentUnsafe());
                return;
            }
            if (channelContext.getChannel().equals(channel)) {
                PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().channelAlreadyIn);
                placeholderComponent.replace("channel", channel);
                placeholderComponent.translateColor();

                bungeecordContext.sendMessage(placeholderComponent.toTextComponentUnsafe());
                return;
            }
            channelContext.setChannel(channel);
            PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().getChannelChange());
            placeholderComponent.replace("channel", channel);
            placeholderComponent.translateColor();

            bungeecordContext.sendMessage(placeholderComponent.toTextComponentUnsafe());
            return;
        }
        String message = String.join(" ", arguments);
        List<ChannelMessage> messageList = AdvanciusBungee.getInstance().getChannelManager().generateMessage(person, channel, message, AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons());
        messageList.forEach(ChannelMessage::send);
    }
}