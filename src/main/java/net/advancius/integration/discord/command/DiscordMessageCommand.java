package net.advancius.integration.discord.command;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.command.CommandCommons;
import net.advancius.command.CommandDescription;
import net.advancius.command.CommandFlags;
import net.advancius.command.CommandHandler;
import net.advancius.command.CommandListener;
import net.advancius.directmessage.DirectMessenger;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.integration.discord.DiscordIntegration;
import net.advancius.integration.discord.DiscordMessage;
import net.advancius.person.Person;
import net.advancius.person.context.ConnectionContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.utils.Metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@FlagManager.FlaggedClass
public class DiscordMessageCommand implements CommandListener {

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.POST_COMMANDS_LOAD)
    public static void command() {
        DiscordMessageCommand command = new DiscordMessageCommand();
        AdvanciusBungee.getInstance().getCommandManager().registerListener(command);
    }

    @CommandHandler(description = "discordmessage")
    public void onCommand(Person sender, CommandDescription description, CommandFlags commandFlags) {
        DiscordIntegration integration = AdvanciusBungee.getInstance().getIntegrationManager().getDiscordIntegration();

        String webhookUrl = integration.getConfiguredWebhookUrl(commandFlags.getFlag("webhook").getValue());
        if (commandFlags.hasFlag("content")) {
            integration.send(commandFlags.getFlag("content").getValue(), webhookUrl);
            if (sender != null) ConnectionContext.sendMessage(sender, AdvanciusLang.getInstance().discordMessageSent);
            return;
        }
        DiscordMessage message = integration.getConfiguredMessage(commandFlags.getFlag("message").getValue());


        Map<String, Object> placeholders = new HashMap<>();
        commandFlags.getCommandFlagList().forEach(commandFlag -> placeholders.put(commandFlag.getKey(), commandFlag.getValue()));

        integration.send(message, webhookUrl, placeholders);
        if (sender != null) ConnectionContext.sendMessage(sender, AdvanciusLang.getInstance().discordMessageSent);
    }
}