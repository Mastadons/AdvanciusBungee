package net.advancius.command.defined;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.command.CommandConfiguration;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.ConnectionContext;
import net.advancius.person.context.PermissionContext;
import net.advancius.placeholder.PlaceholderComponentBuilder;
import net.advancius.utils.ColorUtils;

import java.io.FileNotFoundException;

@FlagManager.FlaggedClass
public class AdvanciusCommand implements CommandListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new AdvanciusCommand());
    }

    @CommandHandler(description = "advanciuschat.reload")
    public void onReloadCommand(Person person, CommandDescription description, String[] arguments) throws Exception {
        try {
            AdvanciusConfiguration.load();
            AdvanciusLang.load();

            PlaceholderComponentBuilder.create(AdvanciusLang.getInstance().reload).sendColored(person);
        } catch (FileNotFoundException exception) {
            throw new Exception("Encountered error reloading", exception);
        }
    }

    @CommandHandler(description = "advanciuschat.help")
    public void onHelpCommand(Person person, CommandDescription description, String[] arguments) throws Exception {
        ConnectionContext.sendMessage(person, "&6&lAdvanciusChat Help Page");
        for (CommandDescription commandDescription : CommandConfiguration.getInstance().getCommands()) {
            if (!PermissionContext.hasPermission(person, commandDescription.getPermission())) continue;
            ConnectionContext.sendMessage(person, "&e" + commandDescription.getSyntax() + " &7» &f" + commandDescription.getDescription());
        }
    }

    @CommandHandler(description = "advanciuschat")
    public void onCommand(Person person, CommandDescription description, String[] arguments) throws Exception {
        if (arguments.length == 0) {
            PlaceholderComponentBuilder.create(AdvanciusLang.getInstance().info).sendColored(person);
            return;
        }

        ConnectionContext.sendMessage(person, ColorUtils.toTextComponent("&cUnknown subcommand."));
    }
}
