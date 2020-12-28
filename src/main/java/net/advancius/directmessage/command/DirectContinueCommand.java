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
import net.advancius.person.context.MetadataContext;
import net.advancius.utils.Metadata;

import java.util.UUID;

@FlagManager.FlaggedClass
public class DirectContinueCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        DirectContinueCommand command = new DirectContinueCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "directcontinue")
    public void onCommand(Person sender, CommandDescription description, String message) throws Exception {
        CommandCommons.checkSyntax(message.isEmpty(), "/continue <message>");
        DirectMessenger.createDirectMessage(sender, getReader(sender), message).sendMessage();
    }

    private Person getReader(Person sender) throws Exception {
        Metadata transientMetadata = MetadataContext.getTransientMetadata(sender);

        CommandCommons.checkCondition(!transientMetadata.isMetadataOf("LastDirectReader", UUID.class),
                AdvanciusLang.getInstance().noRecentReader);

        return CommandCommons.getPerson(transientMetadata.getMetadata("LastDirectReader", UUID.class));
    }
}