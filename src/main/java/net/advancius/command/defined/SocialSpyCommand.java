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
public class SocialSpyCommand implements CommandListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        SocialSpyCommand command = new SocialSpyCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "socialspy")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        MetadataContext metadata = person.getContextManager().getContext(MetadataContext.class);
        metadata.setSocialSpy(!metadata.isSocialSpy());

        PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().socialSpyToggle);
        component.replace("status", metadata.isSocialSpy() ? "enabled" : "disabled");
        component.replace("person", person);
        component.translateColor();
        component.send(person);
    }
}