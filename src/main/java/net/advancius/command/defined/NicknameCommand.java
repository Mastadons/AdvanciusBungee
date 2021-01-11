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
import net.advancius.person.context.ConnectionContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.utils.ColorUtils;

@FlagManager.FlaggedClass
public class NicknameCommand implements CommandListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        NicknameCommand command = new NicknameCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "nickname")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        MetadataContext metadata = person.getContextManager().getContext(MetadataContext.class);

        if (argument == null || argument.isEmpty()) {
            metadata.setNickname(null);
            ConnectionContext.sendMessage(person, ColorUtils.toTextComponent(AdvanciusLang.getInstance().nicknameReset));
            return;
        }

        CommandCommons.checkCondition(argument.length() < AdvanciusConfiguration.getInstance().minNicknameLength,
                AdvanciusLang.getInstance().nicknameMin);

        CommandCommons.checkCondition(argument.length() > AdvanciusConfiguration.getInstance().maxNicknameLength,
                AdvanciusLang.getInstance().nicknameMax);

        metadata.setNickname(argument);

        PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().nicknameChanged);
        component.replace("nickname", argument);
        component.translateColor();
        component.send(person);
    }
}