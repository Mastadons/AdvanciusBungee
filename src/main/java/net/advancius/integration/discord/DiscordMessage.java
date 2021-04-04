package net.advancius.integration.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;

import java.util.Map;

@Data
public class DiscordMessage {

    private String message;
    private String username;
    private String avatarUrl;

    private boolean mentions;

    private DiscordMessageEmbed[] embeds;

    public static JsonObject getUnallowedMentionsJsonObject() {
        JsonObject object = new JsonObject();
        object.add("parse", new JsonArray());
        return object;
    }

    public JsonArray getEmbedsAsJsonArray(Map<String, Object> placeholders) {
        JsonArray array = new JsonArray();

        for (int i = 0; i < embeds.length; i++) {
            array.add(embeds[i].toJsonObject(placeholders));
        }
        return array;
    }

    @Data
    public static class DiscordMessageEmbed {

        private String title;
        private String message;
        private int color;

        public JsonObject toJsonObject(Map<String, Object> placeholders) {
            JsonObject object = new JsonObject();

            object.addProperty("title", DiscordIntegration.replacePlaceholders(title, placeholders));
            object.addProperty("type", "rich");
            object.addProperty("description", DiscordIntegration.replacePlaceholders(message, placeholders));
            object.addProperty("color", color);

            return object;
        }
    }
}
