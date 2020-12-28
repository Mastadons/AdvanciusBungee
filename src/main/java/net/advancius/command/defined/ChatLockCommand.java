package net.advancius.command.defined;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.ConnectionContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.utils.ColorUtils;

@FlagManager.FlaggedClass
public class ChatLockCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new ChatLockCommand());
    }

    @CommandHandler(description = "chatlock")
    public void onCommand(Person person, CommandDescription description, String[] arguments) throws Exception {
        if (arguments.length == 0) {
            PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().incorrectSyntax);
            component.replace("syntax", "/chatlock <channel>");
            component.translateColor();
            component.send(person);
            return;
        }

        ConfiguredChannel channel = AdvanciusBungee.getInstance().getChannelManager().getChannel(arguments[0]);
        if (channel == null) {
            ConnectionContext.sendMessage(person, ColorUtils.toTextComponent(AdvanciusLang.getInstance().unknownChannel));
            return;
        }

        channel.getMetadata().setMetadata("locked", !channel.getMetadata().hasMetadata("locked", true));

        PlaceholderComponent component = new PlaceholderComponent(channel.getMetadata().hasMetadata("locked", true) ?
                AdvanciusLang.getInstance().chatLocked : AdvanciusLang.getInstance().chatUnlocked);
        component.replace("person", person);
        component.replace("channel", channel);
        component.translateColor();
        AdvanciusBungee.getInstance().getPersonManager().broadcastMessage(component.toTextComponent());
    }
}
