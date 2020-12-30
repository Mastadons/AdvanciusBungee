package net.advancius.command.defined;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.ConnectionContext;
import net.advancius.person.context.PermissionContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.placeholder.PlaceholderComponentBuilder;

@FlagManager.FlaggedClass
public class StaffListCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new StaffListCommand());
    }

    @CommandHandler(description = "stafflist")
    public void onCommand(Person person, CommandDescription description, String argument) {
        ConnectionContext.sendMessage(person, AdvanciusLang.getInstance().staffListHeader);
        for (Person onlinePerson : AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons(this::isPersonStaff)) {
            PlaceholderComponentBuilder.create(AdvanciusLang.getInstance().staffListLine)
                    .replace("person", onlinePerson).sendColored(person);
        }
        ConnectionContext.sendMessage(person, AdvanciusLang.getInstance().staffListFooter);
    }

    private boolean isPersonStaff(Person person) {
        return PermissionContext.hasPermission(person, "advancius.staff");
    }
}