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
import net.advancius.person.context.BungeecordContext;
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
            PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().incorrectSyntax);
            placeholderComponent.replace("syntax", "/chatlock <channel>");
            placeholderComponent.translateColor();

            BungeecordContext bungeecordContext = person.getContextManager().getContext("bungeecord");
            bungeecordContext.sendMessage(placeholderComponent.toTextComponentUnsafe());
            return;
        }
        ConfiguredChannel channel = AdvanciusBungee.getInstance().getChannelManager().getChannel(arguments[0]);
        if (channel == null) {
            BungeecordContext bungeecordContext = person.getContextManager().getContext("bungeecord");
            bungeecordContext.sendMessage(ColorUtils.toTextComponent(AdvanciusLang.getInstance().unknownChannel));
            return;
        }

        if (channel.getMetadata().hasMetadata("locked", true)) {
            channel.getMetadata().setMetadata("locked", false);

            PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().chatUnlocked);
            placeholderComponent.replace("person", person);
            placeholderComponent.replace("channel", channel);
            placeholderComponent.translateColor();
            AdvanciusBungee.getInstance().getPersonManager().broadcastMessage(placeholderComponent.toTextComponentUnsafe());
        }
        else {
            channel.getMetadata().setMetadata("locked", true);

            PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().chatLocked);
            placeholderComponent.replace("person", person);
            placeholderComponent.replace("channel", channel);
            placeholderComponent.translateColor();
            AdvanciusBungee.getInstance().getPersonManager().broadcastMessage(placeholderComponent.toTextComponentUnsafe());
        }
    }
}
