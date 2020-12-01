package net.advancius.command.defined;

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
public class SocialSpyCommand implements CommandListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        SocialSpyCommand command = new SocialSpyCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "socialspy")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        MetadataContext mc = person.getContextManager().getContext(MetadataContext.class);
        mc.setSocialSpy(!mc.isSocialSpy());

        PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().socialSpyToggle);
        placeholderComponent.replace("status", mc.isSocialSpy() ? "enabled" : "disabled");
        placeholderComponent.replace("person", person);
        placeholderComponent.translateColor();

        BungeecordContext bc = person.getContextManager().getContext(BungeecordContext.class);
        bc.sendMessage(placeholderComponent.toTextComponentUnsafe());
    }
}