package net.advancius.integration.discord;

import com.google.gson.JsonObject;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLogger;
import net.advancius.event.EventListener;
import net.advancius.flag.FlagManager;
import net.advancius.integration.discord.listener.DiscordListener;
import net.advancius.placeholder.PlaceholderComponent;
import net.md_5.bungee.api.ProxyServer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.util.HashMap;
import java.util.Map;

@FlagManager.FlaggedClass
public class DiscordIntegration implements EventListener {

    private HttpClient connection;

    public void load() throws Exception {
        connection = new HttpClient(new SslContextFactory(true));
        connection.start();

        AdvanciusBungee.getInstance().getEventManager().registerListener(new DiscordListener(this));
    }

    public DiscordMessage getConfiguredMessage(String name) {
        return AdvanciusConfiguration.getInstance().getDiscordIntegration().messages.get(name);
    }

    public String getConfiguredWebhookUrl(String name) {
        return AdvanciusConfiguration.getInstance().getDiscordIntegration().webhooks.get(name);
    }

    public void send(String message, String webhookUrl) {
        ProxyServer.getInstance().getScheduler().runAsync(AdvanciusBungee.getInstance(), () -> {
            try {
                Request request = connection.POST(webhookUrl);
                request.header(HttpHeader.CONTENT_TYPE, "application/json");

                JsonObject requestData = new JsonObject();
                requestData.addProperty("content", message);

                request.content(new StringContentProvider(requestData.toString()), "application/json");
                request.send();

                AdvanciusLogger.info("Successfully sent content through discord integration.");
            } catch (Exception exception) {
                AdvanciusLogger.warn("Encountered exception sending content through discord integration.");
                exception.printStackTrace();
            }
        });
    }

    public void send(DiscordMessage message, String webhookUrl) {
        send(message, webhookUrl, new HashMap<>());
    }

    public void send(DiscordMessage message, String webhookUrl, Map<String, Object> placeholders) {
        ProxyServer.getInstance().getScheduler().runAsync(AdvanciusBungee.getInstance(), () -> {
            try {
                Request request = connection.POST(webhookUrl);
                request.header(HttpHeader.CONTENT_TYPE, "application/json");

                JsonObject requestData = new JsonObject();
                requestData.addProperty("content", replacePlaceholders(message.getMessage(), placeholders));

                if (message.getUsername() != null) requestData.addProperty("username", replacePlaceholders(message.getUsername(), placeholders));
                if (message.getAvatarUrl() != null) requestData.addProperty("avatar_url", message.getAvatarUrl());
                if (!message.isMentions()) requestData.add("allowed_mentions", DiscordMessage.getUnallowedMentionsJsonObject());

                if (message.getEmbeds() != null) requestData.add("embeds", message.getEmbedsAsJsonArray(placeholders));
                System.out.println(requestData.toString());

                request.content(new StringContentProvider(requestData.toString()), "application/json");
                request.send();

                AdvanciusLogger.info("Successfully sent content through discord integration.");
            } catch (Exception exception) {
                AdvanciusLogger.warn("Encountered exception sending content through discord integration.");
                exception.printStackTrace();
            }
        });
    }

    public static String replacePlaceholders(String content, Map<String, Object> placeholders) {
        PlaceholderComponent component = new PlaceholderComponent(content);
        placeholders.forEach(component::replace);

        return component.getText();
    }
}