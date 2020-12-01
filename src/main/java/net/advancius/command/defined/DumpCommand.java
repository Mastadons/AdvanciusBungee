package net.advancius.command.defined;

import net.advancius.AdvanciusBungee;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandFlags;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@FlagManager.FlaggedClass
public class DumpCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new DumpCommand());
    }

    @CommandHandler(description = "dump")
    public void onCommand(Person person, CommandDescription description, CommandFlags commandFlags) throws Exception {
        Person target = null;
        if (commandFlags.hasFlag("person")) {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(commandFlags.getFlag("person").getValue());
            target = AdvanciusBungee.getInstance().getPersonManager().getPersonUnsafe(proxiedPlayer);
        }

        if (commandFlags.hasFlag("uuid")) {
            target = AdvanciusBungee.getInstance().getPersonManager().getPerson(UUID.fromString(commandFlags.getFlag("uuid").getValue()));
        }

        PlaceholderComponent placeholderComponent = new PlaceholderComponent("{target." + commandFlags.getFlag("path").getValue() + '}');
        placeholderComponent.setReplaceJson(commandFlags.hasFlag("json"));
        placeholderComponent.replace("target", target);
        placeholderComponent.translateColor();

        BungeecordContext bungeecordContext = person.getContextManager().getContext("bungeecord");
        bungeecordContext.sendMessage(placeholderComponent.toTextComponentUnsafe());
    }
}
