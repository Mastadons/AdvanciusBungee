package net.advancius.command.defined;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.MetadataContext;
import net.advancius.placeholder.PlaceholderComponent;

@FlagManager.FlaggedClass
public class CommandSpyCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        CommandSpyCommand command = new CommandSpyCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "commandspy")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        MetadataContext metadata = person.getContextManager().getContext(MetadataContext.class);
        metadata.setCommandSpy(!metadata.isCommandSpy());

        PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().commandSpyToggle);
        component.replace("status", metadata.isCommandSpy() ? "enabled" : "disabled");
        component.replace("person", person);
        component.translateColor();
        component.send(person);
    }
}