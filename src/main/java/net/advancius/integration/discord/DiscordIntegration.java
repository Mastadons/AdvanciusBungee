package net.advancius.integration.discord;

import com.google.gson.JsonObject;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLogger;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.channel.message.event.MessagePostSendEvent;
import net.advancius.event.EventHandler;
import net.advancius.event.EventListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.context.MetadataContext;
import net.advancius.person.event.PersonJoinEvent;
import net.advancius.person.event.PersonMoveEvent;
import net.advancius.person.event.PersonQuitEvent;
import net.advancius.placeholder.PlaceholderComponentBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;

@FlagManager.FlaggedClass
public class DiscordIntegration implements EventListener {

    private static DiscordIntegration instance;

    @FlagManager.FlaggedMethod(priority = 40, flag = DefinedFlag.PLUGIN_LOAD)
    public static void loadIntegration() throws Exception {
        AdvanciusLogger.info("Establishing Discord integration.");

        instance = new DiscordIntegration();
        instance.load();
    }

    @FlagManager.FlaggedMethod(priority = 0, flag = DefinedFlag.PLUGIN_SAVE)
    public static void stopIntegration() throws Exception {
        instance.stop();
    }

    private HttpClient connection;

    private void load() throws Exception {
        connection = new HttpClient(new SslContextFactory(true));
        connection.start();

        AdvanciusBungee.getInstance().getEventManager().registerListener(this);
    }

    private void stop() throws Exception {
        connection.stop();
    }

    @EventHandler
    public void onMessagePostSend(MessagePostSendEvent event) {
        if (AdvanciusConfiguration.getInstance().discordIntegration == null) return;

        if (!(event.getMessage().getChannel() instanceof ConfiguredChannel)) return;
        if (!event.getMessage().getSender().equals(event.getMessage().getReader())) return;

        ConfiguredChannel channel = (ConfiguredChannel) event.getMessage().getChannel();

        String message = ChatColor.stripColor(event.getFormattedMessage().toPlainText());

        AdvanciusConfiguration.getInstance().discordIntegration.channelWebhooks.forEach(channelWebhook -> {
            if (!channel.getName().equalsIgnoreCase(channelWebhook.channel)) return;

            String content = PlaceholderComponentBuilder.create(channelWebhook.message)
                    .replace("message", message)
                    .replace("event", event)
                    .getPlaceholderComponent().getText();

            sendWebhookContent(channelWebhook.url, content);
        });
    }

    @EventHandler
    public void onPersonJoin(PersonJoinEvent event) {
        if (AdvanciusConfiguration.getInstance().discordIntegration == null) return;
        if (event.getPerson().getContextManager().getContext(MetadataContext.class).isSilent()) return;

        AdvanciusConfiguration.getInstance().discordIntegration.joinWebhooks.forEach(webhook -> {
            String content = PlaceholderComponentBuilder.create(webhook.message)
                    .replace("event", event)
                    .getPlaceholderComponent().getText();

            sendWebhookContent(webhook.url, content);
        });
    }

    @EventHandler
    public void onPersonQuit(PersonQuitEvent event) {
        if (AdvanciusConfiguration.getInstance().discordIntegration == null) return;
        if (event.getPerson().getContextManager().getContext(MetadataContext.class).isSilent()) return;

        AdvanciusConfiguration.getInstance().discordIntegration.quitWebhooks.forEach(webhook -> {
            String content = PlaceholderComponentBuilder.create(webhook.message)
                    .replace("event", event)
                    .getPlaceholderComponent().getText();

            sendWebhookContent(webhook.url, content);
        });
    }

    @EventHandler
    public void onPersonMove(PersonMoveEvent event) {
        if (AdvanciusConfiguration.getInstance().discordIntegration == null) return;
        if (!MetadataContext.getTransientMetadata(event.getPerson()).hasMetadata("ending_server")) return;
        if (event.getPerson().getContextManager().getContext(MetadataContext.class).isSilent()) return;

        AdvanciusConfiguration.getInstance().discordIntegration.moveWebhooks.forEach(webhook -> {
            String content = PlaceholderComponentBuilder.create(webhook.message)
                    .replace("event", event)
                    .getPlaceholderComponent().getText();

            sendWebhookContent(webhook.url, content);
        });
    }

    private void sendWebhookContent(String url, String content) {
        ProxyServer.getInstance().getScheduler().runAsync(AdvanciusBungee.getInstance(), () -> {
            try {
                Request request = connection.POST(url);
                request.header(HttpHeader.CONTENT_TYPE, "application/json");

                JsonObject requestData = new JsonObject();
                requestData.addProperty("content", content);

                request.content(new StringContentProvider(requestData.toString()), "application/json");
                request.send();
            } catch (Exception exception) {
                AdvanciusLogger.warn("Encountered exception sending content to discord integration.");
                exception.printStackTrace();
            }
        });
    }
}