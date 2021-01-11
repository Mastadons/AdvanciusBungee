package net.advancius.channel.configured;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.person.Person;
import net.advancius.placeholder.PlaceholderComponentBuilder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

public class ChannelCommand extends Command {

    private final ConfiguredChannel channel;

    public ChannelCommand(ConfiguredChannel channel) {
        super(channel.getName(), channel.getGuard().getSendPermission(), getCommandAliases(channel));
        this.channel = channel;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(((ProxiedPlayer) sender).getUniqueId());

        PlaceholderComponentBuilder.create(AdvanciusLang.getInstance().channelChange)
                .replace("channel", channel).sendColored(person);
    }

    private static String[] getCommandAliases(ConfiguredChannel channel) {
        List<String> aliases = new ArrayList<>();
        if (channel.getMetadata().hasMetadata("aliases"))
            aliases = (List<String>) channel.getMetadata().getMetadata("aliases");

        return aliases.toArray(new String[0]);
    }
}
