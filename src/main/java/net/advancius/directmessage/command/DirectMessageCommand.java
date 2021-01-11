package net.advancius.directmessage.command;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.command.CommandCommons;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.directmessage.DirectMessenger;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;

import java.util.Arrays;

@FlagManager.FlaggedClass
public class DirectMessageCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        DirectMessageCommand command = new DirectMessageCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "directmessage")
    public void onCommand(Person sender, CommandDescription description, String[] arguments) throws Exception {
        CommandCommons.checkSyntax(arguments.length < 2, "/message <player> <message>");

        String message = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
        Person reader = CommandCommons.getPerson(arguments[0]);

        CommandCommons.checkCondition(sender.equals(reader), AdvanciusLang.getInstance().cannotMessageSelf);
        DirectMessenger.createDirectMessage(sender, reader, message).sendMessage();
    }
}