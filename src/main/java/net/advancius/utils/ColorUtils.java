package net.advancius.utils;

import net.advancius.AdvanciusConfiguration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class ColorUtils {

    public static String translateColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static TextComponent toTextComponent(String message) {
        return new TextComponent(TextComponent.fromLegacyText(ColorUtils.translateColor(message)));
    }

    public static TextComponent toTextComponent(String message, Object... placeholders) {
        return new TextComponent(TextComponent.fromLegacyText(ColorUtils.translateColor(Placeholders.replace(message, placeholders))));
    }

    public static ChatColor getColor(String input) {
        input = input.replace(" ", "");

        for (ChatColor chatColor : ChatColor.values()) {
            List<String> aliases = AdvanciusConfiguration.getInstance().colorAliases.get(chatColor.name());
            if (aliases == null) continue;

            for (String alias : aliases) if (input.equalsIgnoreCase(alias)) return chatColor;
            if (chatColor.getName().equalsIgnoreCase(input)) return chatColor;
        }
        return null;
    }

    public static String getFancyName(ChatColor color) {
        return color.name().replace('_', ' ').toLowerCase();
    }
}
