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
import net.advancius.person.context.PermissionContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.md_5.bungee.api.chat.TextComponent;

@FlagManager.FlaggedClass
public class ChatClearCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new ChatClearCommand());
    }

    @CommandHandler(description = "chatclear")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().chatCleared);
        placeholderComponent.replace("person", person);
        placeholderComponent.translateColor();

        for (Person onlinePerson : AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons(o -> !isExempt(o))) {
            for (int i=0; i<200; i++) BungeecordContext.sendMessage(onlinePerson, new TextComponent(" \n "));
        }
        AdvanciusBungee.getInstance().getPersonManager().broadcastMessage(placeholderComponent.toTextComponentUnsafe());
    }

    private boolean isExempt(Person person) {
        String permission = AdvanciusBungee.getInstance().getCommandManager().getDescription("chatclear").getPermission();
        return person.getContextManager().getContext(PermissionContext.class).hasPermission(permission);
    }
}