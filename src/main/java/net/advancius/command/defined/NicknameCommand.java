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

@FlagManager.FlaggedClass
public class NicknameCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        NicknameCommand command = new NicknameCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "nickname")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        BungeecordContext bc = person.getContextManager().getContext("bungeecord");
        MetadataContext mc = person.getContextManager().getContext(MetadataContext.class);

        if (argument == null || argument.isEmpty()) {
            mc.setNickname(null);
            bc.sendMessage(ColorUtils.toTextComponent(AdvanciusLang.getInstance().nicknameReset));
            return;
        }

        CommandCommons.checkCondition(argument.length() < AdvanciusConfiguration.getInstance().minNicknameLength,
                AdvanciusLang.getInstance().nicknameMin);

        CommandCommons.checkCondition(argument.length() > AdvanciusConfiguration.getInstance().maxNicknameLength,
                AdvanciusLang.getInstance().nicknameMax);

        mc.setNickname(argument);

        PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().nicknameChanged);
        placeholderComponent.replace("nickname", argument);
        placeholderComponent.translateColor();

        bc.sendMessage(placeholderComponent.toTextComponentUnsafe());
    }
}