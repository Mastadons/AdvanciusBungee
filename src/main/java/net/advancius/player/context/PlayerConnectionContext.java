package net.advancius.player.context;

import com.google.gson.JsonObject;
import lombok.Data;
import net.advancius.person.context.ConnectionContext;
import net.advancius.utils.ColorUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Data
public class PlayerConnectionContext extends ConnectionContext {

    private ProxiedPlayer proxiedPlayer;

    @Override
    public void sendMessage(TextComponent component) {
        proxiedPlayer.sendMessage(component);
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        ProxyServer.getInstance().createTitle()
            .title(ColorUtils.toTextComponent(title))
            .subTitle(ColorUtils.toTextComponent(subtitle))
            .fadeIn(fadeIn)
            .stay(stay)
            .fadeOut(fadeOut)
            .send(proxiedPlayer);
    }

    @Override
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
    public String getConnectionName() {
        return proxiedPlayer.getName();
    }

    @Override
    public void onPersonSave() {}
}
