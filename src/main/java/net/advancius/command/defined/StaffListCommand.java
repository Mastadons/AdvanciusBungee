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

@FlagManager.FlaggedClass
public class StaffListCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new StaffListCommand());
    }

    @CommandHandler(description = "stafflist")
    public void onCommand(Person person, CommandDescription description, String argument) {
        BungeecordContext bc0 = person.getContextManager().getContext(BungeecordContext.class);

        bc0.sendMessage(AdvanciusLang.getInstance().staffListHeader);
        for (Person onlinePerson : AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons(this::isPersonStaff)) {
            PlaceholderComponent pc = new PlaceholderComponent(AdvanciusLang.getInstance().staffListLine);
            pc.replace("person", onlinePerson);
            pc.translateColor();

            bc0.sendMessage(pc.toTextComponentUnsafe());
        }
        bc0.sendMessage(AdvanciusLang.getInstance().staffListFooter);
    }

    private boolean isPersonStaff(Person person) {
        return person.getContextManager().getContext(PermissionContext.class).hasPermission("advancius.staff");
    }
}