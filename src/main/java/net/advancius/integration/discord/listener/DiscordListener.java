package net.advancius.integration.discord.listener;

import lombok.Data;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.channel.message.event.MessagePostSendEvent;
import net.advancius.event.Event;
import net.advancius.event.EventHandler;
import net.advancius.event.EventListener;
import net.advancius.integration.discord.DiscordIntegration;
import net.advancius.integration.discord.DiscordMessage;
import net.advancius.person.context.MetadataContext;
import net.advancius.person.event.PersonJoinEvent;
import net.advancius.person.event.PersonMoveEvent;
import net.advancius.person.event.PersonQuitEvent;

import java.util.HashMap;
import java.util.Map;

@Data
public class DiscordListener implements EventListener {

    private final DiscordIntegration integration;

    @EventHandler
    public void onMessagePostSend(MessagePostSendEvent event) {
        if (!event.getMessage().getSender().equals(event.getMessage().getReader())) return;
        if (!(event.getMessage().getChannel() instanceof ConfiguredChannel)) return;
        ConfiguredChannel channel = (ConfiguredChannel) event.getMessage().getChannel();

        if (!channel.getMetadata().hasMetadata("discord_integration_message")) return;

        String messageName = channel.getMetadata().getMetadata("discord_integration_message");
        String webhookName = channel.getMetadata().getMetadata("discord_integration_webhook");

        DiscordMessage message = integration.getConfiguredMessage(messageName);
        String webhookUrl = integration.getConfiguredWebhookUrl(webhookName);

        if (message == null || webhookUrl == null) return;

        integration.send(message, webhookUrl, getEventPlaceholder(event));
    }

    private Map<String, Object> getEventPlaceholder(Event event) {
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("event", event);

        return placeholders;
    }

    @EventHandler
    public void onPersonJoin(PersonJoinEvent event) {
        if (event.getPerson().getContextManager().getContext(MetadataContext.class).isSilent()) {
            DiscordMessage message = integration.getConfiguredMessage("internal.silent_join");
            String webhookUrl = integration.getConfiguredWebhookUrl("internal");

            if (message == null || webhookUrl == null) return;

            integration.send(message, webhookUrl, getEventPlaceholder(event));
            return;
        }

        DiscordMessage message = integration.getConfiguredMessage("internal.join");
        String webhookUrl = integration.getConfiguredWebhookUrl("internal");

        if (message == null || webhookUrl == null) return;

        integration.send(message, webhookUrl, getEventPlaceholder(event));
    }

    @EventHandler
    public void onPersonQuit(PersonQuitEvent event) {
        if (event.getPerson().getContextManager().getContext(MetadataContext.class).isSilent()) {
            DiscordMessage message = integration.getConfiguredMessage("internal.silent_quit");
            String webhookUrl = integration.getConfiguredWebhookUrl("internal");

            if (message == null || webhookUrl == null) return;

            integration.send(message, webhookUrl, getEventPlaceholder(event));
            return;
        }

        DiscordMessage message = integration.getConfiguredMessage("internal.quit");
        String webhookUrl = integration.getConfiguredWebhookUrl("internal");

        if (message == null || webhookUrl == null) return;

        integration.send(message, webhookUrl, getEventPlaceholder(event));
    }

    @EventHandler(500)
    public void onPersonMove(PersonMoveEvent event) {
        if (!MetadataContext.getTransientMetadata(event.getPerson()).hasMetadata("ending_server")) return;

        if (event.getPerson().getContextManager().getContext(MetadataContext.class).isSilent()) {
            DiscordMessage message = integration.getConfiguredMessage("internal.silent_move");
            String webhookUrl = integration.getConfiguredWebhookUrl("internal");

            if (message == null || webhookUrl == null) return;

            integration.send(message, webhookUrl, getEventPlaceholder(event));
            return;
        }

        DiscordMessage message = integration.getConfiguredMessage("internal.move");
        String webhookUrl = integration.getConfiguredWebhookUrl("internal");

        if (message == null || webhookUrl == null) return;

        integration.send(message, webhookUrl, getEventPlaceholder(event));
    }
}
