package net.advancius.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import net.advancius.AdvanciusBungee;

import java.util.ArrayList;
import java.util.List;

public class Placeholders {


    public static String replace(String original, Object... placeholderObjects) {
        if (placeholderObjects.length == 0) return original;

        List<Placeholder> placeholders = createPlaceholders(placeholderObjects);
        StringBuilder replaced = new StringBuilder();
        outer_loop:
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (character == '{') {
                StringBuilder placeholderText = new StringBuilder();
                char nextCharacter = character;
                while (i < original.length() && nextCharacter != '}') {
                    nextCharacter = original.charAt(i++);
                    placeholderText.append(nextCharacter);
                }
                if (placeholderText.length() == 2) return original;
                placeholderText = new StringBuilder(placeholderText.substring(1, placeholderText.length() - 1));
                for (Placeholder placeholder : placeholders) {
                    String replacement = placeholder.getReplacement(placeholderText.toString());
                    if (replacement == null) continue;
                    replaced.append(replacement);
                    i--;
                    continue outer_loop;
                }
                replaced.append('{').append(placeholderText).append('}');
            } else replaced.append(character);
        }
        return replaced.toString();
    }

    public static List<Placeholder> createPlaceholders(Object... placeholderObjects) {
        List<Placeholder> placeholders = new ArrayList<>();
        for (int i = 0; i < placeholderObjects.length - 1; i++) {
            if (placeholderObjects[i + 1] == null || placeholderObjects[i + 1].getClass() == String.class)
                placeholders.add(TextPlaceholder.createPlaceholder(placeholderObjects[i], placeholderObjects[++i]));
            else placeholders.add(JsonPlaceholder.createPlaceholder(placeholderObjects[i], placeholderObjects[++i]));
        }
        return placeholders;
    }

    public interface Placeholder {
        String getReplacement(String placeholder);
    }

    @Data
    public static class TextPlaceholder implements Placeholder {

        private final String name;
        private final String text;

        public static Placeholder createPlaceholder(Object nameObject, Object textObject) {
            if (textObject == null) textObject = "";
            return new TextPlaceholder(String.valueOf(nameObject), String.valueOf(textObject));
        }

        public String getReplacement(String placeholder) {
            if (placeholder.equals(name)) return text;
            return null;
        }
    }

    @Data
    public static class JsonPlaceholder implements Placeholder {

        private final String name;
        private final JsonObject json;

        public static Placeholder createPlaceholder(Object nameObject, Object jsonObject) {
            if (jsonObject.getClass().equals(JsonObject.class))
                return new JsonPlaceholder(nameObject.toString(), (JsonObject) jsonObject);
            JsonObject json = new JsonParser().parse(AdvanciusBungee.GSON.toJson(jsonObject)).getAsJsonObject();
            return new JsonPlaceholder(nameObject.toString(), json);
        }

        public String getReplacement(String placeholder) {
            String[] components = placeholder.split("\\.");
            if (components.length == 0) return null;
            if (!components[0].equals(name)) return null;

            JsonElement relativeElement = json;
            for (int i = 1; i < components.length; i++) {
                if (!(relativeElement instanceof JsonObject)) return relativeElement.getAsString();
                relativeElement = ((JsonObject) relativeElement).get(components[i]);
            }
            return relativeElement.getAsString();
        }
    }
}
