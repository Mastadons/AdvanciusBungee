package net.advancius.command.defined;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.channel.ChannelConfiguration;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.utils.ColorUtils;

import java.io.FileNotFoundException;

@FlagManager.FlaggedClass
public class AdvanciusCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new AdvanciusCommand());
    }

    @CommandHandler(description = "advanciuschat.reload")
    public void onReloadCommand(Person person, CommandDescription description, String[] arguments) throws Exception {
        try {
            AdvanciusConfiguration.load();
            AdvanciusLang.load();

            PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().reload);
            placeholderComponent.translateColor();

            BungeecordContext bungeecordContext = person.getContextManager().getContext("bungeecord");
            bungeecordContext.sendMessage(placeholderComponent.toTextComponentUnsafe());
        } catch (FileNotFoundException exception) {
            throw new Exception("Encountered error reloading", exception);
        }
    }

    @CommandHandler(description = "advanciuschat")
    public void onCommand(Person person, CommandDescription description, String[] arguments) throws Exception {
        if (arguments.length == 0) {
            PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().info);
            placeholderComponent.translateColor();

            BungeecordContext bungeecordContext = person.getContextManager().getContext("bungeecord");
            bungeecordContext.sendMessage(placeholderComponent.toTextComponentUnsafe());
            return;
        }

        BungeecordContext bungeecordContext = person.getContextManager().getContext("bungeecord");
        bungeecordContext.sendMessage(ColorUtils.toTextComponent("&cUnknown subcommand."));
    }
}
