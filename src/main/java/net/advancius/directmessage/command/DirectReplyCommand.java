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

import java.util.UUID;

@FlagManager.FlaggedClass
public class DirectReplyCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        DirectReplyCommand command = new DirectReplyCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "directreply")
    public void onCommand(Person sender, CommandDescription description, String message) throws Exception {
        CommandCommons.checkSyntax(message.isEmpty(), "/reply <message>");
        DirectMessenger.createDirectMessage(sender, getReader(sender), message).sendMessage();
    }

    private Person getReader(Person sender) throws Exception {
        MetadataContext metadataContext = sender.getContextManager().getContext("metadata");

        CommandCommons.checkCondition(!metadataContext.getTransientMetadata().isMetadataOf("LastDirectSender", UUID.class),
                AdvanciusLang.getInstance().noRecentSender);

        return CommandCommons.getPerson(metadataContext.getTransientMetadata().getMetadata("LastDirectSender", UUID.class));
    }
}