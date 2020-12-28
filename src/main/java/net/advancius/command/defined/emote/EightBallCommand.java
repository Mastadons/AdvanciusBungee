package net.advancius.command.defined.emote;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.channel.message.ChannelMessage;
import net.advancius.command.CommandCommons;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.MetadataContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.utils.Commons;

import java.util.List;

@FlagManager.FlaggedClass
public class EightBallCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        AdvanciusBungee.getInstance().getCommandManager().registerListener(new EightBallCommand());
    }

    @CommandHandler(description = "eightball")
    public void onCommand(Person person, CommandDescription description, String argument) throws Exception {
        CommandCommons.checkCondition(!isCorrectChannel(person), AdvanciusLang.getInstance().eightBallIncorrectChannel);

        long previous = MetadataContext.getTransientMetadata(person).getMetadataOr("LastEightBall", 0L);
        long cooldown = AdvanciusConfiguration.getInstance().eightBallCooldown;
        if (Commons.onCooldown(person, previous, cooldown, true)) return;

        PlaceholderComponent pc = new PlaceholderComponent(AdvanciusLang.getInstance().eightBallFormat);
        pc.replace("response", randomEightBallResponse());
        pc.translateColor();

        AdvanciusBungee.getInstance().getChannelManager().generateMessage(person, "Magic 8Ball, " + argument).forEach(ChannelMessage::send);
        AdvanciusBungee.getInstance().getPersonManager().broadcastMessage(pc.toTextComponent());
        MetadataContext.getTransientMetadata(person).setMetadata("LastEightBall", System.currentTimeMillis());
    }

    private static boolean isCorrectChannel(Person person) {
        ConfiguredChannel channel = AdvanciusBungee.getInstance().getChannelManager().getChannel(person);
        String correctChannel = AdvanciusConfiguration.getInstance().getEightBallChannel();

        return channel.getName().equalsIgnoreCase(correctChannel);
    }

    private static String randomEightBallResponse() {
        List<String> responses = AdvanciusLang.getInstance().eightBallResponses;
        return responses.get((int)(Math.random() * responses.size()));
    }
}