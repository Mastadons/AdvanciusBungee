package net.advancius.person.context;

import lombok.Data;
import lombok.NonNull;
import net.advancius.person.Person;
import net.advancius.utils.ColorUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

@Data
public abstract class ConnectionContext extends PersonContext {

    public static void sendMessage(@NonNull Person person, TextComponent component) {
        person.getContextManager().getContext(ConnectionContext.class).sendMessage(component);
    }

    public static void sendMessage(@NonNull Person person, String message) {
        person.getContextManager().getContext(ConnectionContext.class).sendMessage(message);
    }

    public abstract void sendMessage(TextComponent component);

    public abstract ServerInfo getServer();

    public abstract void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);

    public abstract String getConnectionName();

    public void sendMessage(String message) {
        sendMessage(ColorUtils.toTextComponent(message));
    }

    @Override
    public final String getName() {
        return "connection";
    }
}
