package net.advancius.command;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.person.Person;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.utils.ColorUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class CommandCommons {

    public static void checkSyntax(boolean errorCondition, String syntax) throws Exception {
        if (!errorCondition) return;
        PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().incorrectSyntax);
        placeholderComponent.replace("syntax", syntax);
        placeholderComponent.translateColor();

        throw new Exception(placeholderComponent.getText());
    }

    public static ConfiguredChannel getChannel(String name) throws Exception {
        ConfiguredChannel channel = AdvanciusBungee.getInstance().getChannelManager().getChannel(name);

        if (channel == null) throw new Exception(AdvanciusLang.getInstance().unknownChannel);
        return channel;
    }

    public static ChatColor getColor(String name) throws Exception {
        ChatColor color = ColorUtils.getColor(name);

        if (color == null) throw new Exception(AdvanciusLang.getInstance().unknownColor);
        return color;
    }

    public static Person getPerson(UUID id) throws Exception {
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(id);

        if (person == null) throw new Exception(AdvanciusLang.getInstance().unknownPlayer);
        return person;
    }

    public static Person getPerson(String name) throws Exception {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(name);
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPersonUnsafe(proxiedPlayer);

        if (person == null) throw new Exception(AdvanciusLang.getInstance().unknownPlayer);
        return person;
    }

    public static void checkCondition(boolean errorCondition, String errorMessage) throws Exception {
        if (errorCondition) throw new Exception(errorMessage);
    }

    public static void checkCondition(boolean errorCondition, PlaceholderComponent placeholderComponent) throws Exception {
        if (errorCondition) throw new Exception(placeholderComponent.getText());
    }
}
