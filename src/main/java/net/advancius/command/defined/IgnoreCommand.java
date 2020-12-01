package net.advancius.command.defined;

import net.advancius.AdvanciusBungee;
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
import net.advancius.person.context.PermissionContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.utils.ColorUtils;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@FlagManager.FlaggedClass
public class IgnoreCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new IgnoreCommand());
    }

    @CommandHandler(description = "ignore")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        Person target = CommandCommons.getPerson(argument);

        CommandCommons.checkCondition(isExempt(target), AdvanciusLang.getInstance().cannotIgnore);

        MetadataContext metadataContext = person.getContextManager().getContext(MetadataContext.class);
        metadataContext.setIgnoring(target.getId(), !metadataContext.isIgnoring(target.getId()));

        PlaceholderComponent pc = new PlaceholderComponent(metadataContext.isIgnoring(target.getId())
                ? AdvanciusLang.getInstance().personIgnored : AdvanciusLang.getInstance().personUnignored);

        pc.replace("person", target);
        pc.translateColor();

        person.getContextManager().getContext(BungeecordContext.class).sendMessage(pc.toTextComponentUnsafe());
    }

    private static boolean isExempt(Person person) {
        PermissionContext permissionContext = person.getContextManager().getContext(PermissionContext.class);

        CachedMetaData metadata = permissionContext.getLuckpermsUser().getCachedData().getMetaData();
        String exemptStatus = metadata.getMetaValue("ignore-exempt");
        return exemptStatus != null && exemptStatus.equalsIgnoreCase("true");
    }
}