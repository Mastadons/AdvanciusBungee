package net.advancius.command.defined;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.command.CommandCommons;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.utils.ColorUtils;
import net.md_5.bungee.api.ChatColor;

@FlagManager.FlaggedClass
public class ChatColorCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        ChatColorCommand command = new ChatColorCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "chatcolor")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        ChatColor color = CommandCommons.getColor(argument);

        CommandCommons.checkCondition(!AdvanciusConfiguration.getInstance().allowedChatColor.contains(color.getName().toUpperCase()),
                AdvanciusLang.getInstance().unallowedColor);

        person.getContextManager().getContext(MetadataContext.class).setChatColor(color);

        PlaceholderComponent pc = new PlaceholderComponent(AdvanciusLang.getInstance().chatColorChanged);
        pc.replace("color", ColorUtils.getFancyName(color));
        pc.translateColor();

        person.getContextManager().getContext(BungeecordContext.class).sendMessage(pc.toTextComponentUnsafe());
    }
}
