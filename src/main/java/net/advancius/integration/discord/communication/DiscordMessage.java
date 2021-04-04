package net.advancius.integration.discord.communication;

import net.advancius.AdvanciusBungee;
import net.advancius.communication.identifier.Identifier;
import net.advancius.communication.packet.Packet;
import net.advancius.communication.packet.PacketHandler;
import net.advancius.communication.packet.PacketListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.integration.discord.DiscordIntegration;
import net.advancius.listener.communication.ClientDumpRequest;

import java.util.HashMap;
import java.util.Map;

public class DiscordMessage implements PacketListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 15)
    public static void load() {
        AdvanciusBungee.getInstance().getCommunicationManager().registerListener(new ClientDumpRequest());
    }

    @PacketHandler(packetType = "discord_message")
    public void onClientDiscordMessage(Identifier clientIdentifier, Packet packet) {
        String messageName = packet.getMetadata().getMetadata("message");
        String webhookName = packet.getMetadata().getMetadata("webhook");

        Map<String, Object> placeholders = new HashMap<>();
        if (packet.getMetadata().hasMetadata("placeholders")) {
            Map<String, String> placeholders0 = packet.getMetadata().getMetadata("placeholders", Map.class);
            placeholders0.forEach(placeholders::put);
        }

        DiscordIntegration integration = AdvanciusBungee.getInstance().getIntegrationManager().getDiscordIntegration();

        net.advancius.integration.discord.DiscordMessage message = integration.getConfiguredMessage(messageName);
        String webhookUrl = integration.getConfiguredWebhookUrl(webhookName);

        if (message == null || webhookUrl == null) return;

        integration.send(message, webhookUrl, placeholders);
    }
}
