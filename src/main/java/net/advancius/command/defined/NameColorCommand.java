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
import net.advancius.person.context.MetadataContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.utils.ColorUtils;
import net.md_5.bungee.api.ChatColor;

@FlagManager.FlaggedClass
public class NameColorCommand implements CommandListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        NameColorCommand command = new NameColorCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "namecolor")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        ChatColor color = CommandCommons.getColor(argument);

        CommandCommons.checkCondition(!AdvanciusConfiguration.getInstance().allowedNameColor.contains(color.getName().toUpperCase()),
                AdvanciusLang.getInstance().unallowedColor);

        person.getContextManager().getContext(MetadataContext.class).setNameColor(color);

        PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().nameColorChanged);
        component.replace("color", ColorUtils.getFancyName(color));
        component.translateColor();
        component.send(person);
    }
}