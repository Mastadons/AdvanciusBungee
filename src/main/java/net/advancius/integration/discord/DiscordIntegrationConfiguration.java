package net.advancius.integration.discord;

import java.util.HashMap;
import java.util.Map;

public class DiscordIntegrationConfiguration {

    public Map<String, String> webhooks = new HashMap<>();
    public HashMap<String, DiscordMessage> messages = new HashMap<>();
}
