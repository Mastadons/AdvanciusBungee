package net.advancius.command.defined.emote;

import net.advancius.AdvanciusBungee;
import net.advancius.channel.message.ChannelMessage;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;

@FlagManager.FlaggedClass
public class TableFlipCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new TableFlipCommand());
    }

    @CommandHandler(description = "tableflip")
    public void onCommand(Person person, CommandDescription description, String argument) {
        AdvanciusBungee.getInstance().getChannelManager().generateMessage(person, "(╯°□°)╯︵ ┻━┻").forEach(ChannelMessage::send);
    }
}