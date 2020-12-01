package net.advancius.command.defined;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.channel.configured.ConfiguredChannel;
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

@FlagManager.FlaggedClass
public class IgnoreChannelCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new IgnoreChannelCommand());
    }

    @CommandHandler(description = "ignorechannel")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        ConfiguredChannel channel = CommandCommons.getChannel(argument);
        MetadataContext metadataContext = person.getContextManager().getContext(MetadataContext.class);

        metadataContext.setIgnoringChannel(channel.getName(), !metadataContext.isIgnoringChannel(channel.getName()));

        PlaceholderComponent pc = new PlaceholderComponent(metadataContext.isIgnoringChannel(channel.getName())
                ? AdvanciusLang.getInstance().channelIgnored : AdvanciusLang.getInstance().channelUnignored);

        pc.replace("channel", channel);
        pc.translateColor();

        person.getContextManager().getContext(BungeecordContext.class).sendMessage(pc.toTextComponentUnsafe());
    }
}
