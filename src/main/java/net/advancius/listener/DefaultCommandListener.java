package net.advancius.listener;

import net.advancius.AdvanciusBungee;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;

@FlagManager.FlaggedClass
public class DefaultCommandListener implements CommandListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 20)
    public static void load() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new DefaultCommandListener());
    }

    @CommandHandler(description = "chico")
    public void chicoCommand(Person person, CommandDescription description, String arguments) throws Exception {
        if (person == null) throw new Exception("Only players can use this command.");

        BungeecordContext bungeecordContext = person.getContextManager().getContext("bungeecord");
        bungeecordContext.sendMessage(arguments);
    }
}
