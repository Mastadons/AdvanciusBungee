package net.advancius.integration.discord;

import java.util.ArrayList;
import java.util.List;

public class DiscordIntegrationConfiguration {

    public List<ChannelWebhook> channelWebhooks = new ArrayList<>();

    public List<EventWebhook> joinWebhooks = new ArrayList<>();
    public List<EventWebhook> quitWebhooks = new ArrayList<>();
    public List<EventWebhook> moveWebhooks = new ArrayList<>();

    public static class ChannelWebhook {

        public String channel;
        public String url;
        public String message;
    }

    public static class EventWebhook {

        public String url;
        public String message;
    }
}
