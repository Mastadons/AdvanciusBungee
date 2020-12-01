package net.advancius.silent;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.placeholder.PlaceholderComponent;

@FlagManager.FlaggedClass
public class SilentCommand implements CommandListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        SilentCommand command = new SilentCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "silent")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        MetadataContext metadataContext = person.getContextManager().getContext(MetadataContext.class);
        metadataContext.setSilent(!metadataContext.isSilent());

        PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().silentToggle);
        placeholderComponent.replace("status", metadataContext.isSilent() ? "enabled" : "disabled");
        placeholderComponent.replace("person", person);
        placeholderComponent.translateColor();

        BungeecordContext bungeecordContext = person.getContextManager().getContext(BungeecordContext.class);
        bungeecordContext.sendMessage(placeholderComponent.toTextComponentUnsafe());
    }
}
