package net.advancius.person.context;

import com.google.gson.JsonObject;
import lombok.Data;
import net.advancius.person.Person;
import net.advancius.utils.ColorUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;


@Data
public class BungeecordContext extends PersonContext {

    public static void sendMessage(Person person, TextComponent component) {
        person.getContextManager().getContext(BungeecordContext.class).sendMessage(component);
    }

    public static void sendMessage(Person person, String message) {
        person.getContextManager().getContext(BungeecordContext.class).sendMessage(message);
    }

    private ProxiedPlayer proxiedPlayer;

    public void sendMessage(TextComponent component) {
        proxiedPlayer.sendMessage(component);
    }

    public void sendMessage(String message) {
        sendMessage(ColorUtils.toTextComponent(message));
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        ProxyServer.getInstance().createTitle()
            .title(ColorUtils.toTextComponent(title))
            .subTitle(ColorUtils.toTextComponent(subtitle))
            .fadeIn(fadeIn)
            .stay(stay)
            .fadeOut(fadeOut)
            .send(proxiedPlayer);
    }

    public ServerInfo getServer() {
        return proxiedPlayer.getServer().getInfo();
    }

    @Override
    public JsonObject serializeJson() {
        return new JsonObject();
    }

    @Override
    public void onPersonLoad() {
        proxiedPlayer = ProxyServer.getInstance().getPlayer(person.getId());
    }

    @Override
    public void onPersonSave() {}

    @Override
    public String getName() {
        return "bungeecord";
    }
}
