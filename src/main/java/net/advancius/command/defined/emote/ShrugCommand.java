package net.advancius.command.defined.emote;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.channel.message.ChannelMessage;
import net.advancius.command.CommandCommons;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.placeholder.PlaceholderComponent;

import java.util.List;

@FlagManager.FlaggedClass
public class ShrugCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new ShrugCommand());
    }

    @CommandHandler(description = "shrug")
    public void onCommand(Person person, CommandDescription description, String argument) {
        AdvanciusBungee.getInstance().getChannelManager().generateMessage(person, "¯\\_(ツ)_/¯").forEach(ChannelMessage::send);
    }
}