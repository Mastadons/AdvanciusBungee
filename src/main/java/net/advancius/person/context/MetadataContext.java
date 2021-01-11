package net.advancius.person.context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.NonNull;
import net.advancius.AdvanciusBungee;
import net.advancius.person.Person;
import net.advancius.utils.Metadata;
import net.md_5.bungee.api.ChatColor;

import java.util.UUID;

@Data
public abstract class MetadataContext extends PersonContext {

    protected Metadata persistentMetadata = new Metadata();
    protected Metadata transientMetadata = new Metadata();

    public static Metadata getPersistentMetadata(@NonNull Person person) {
        return person.getContextManager().getContext(MetadataContext.class).persistentMetadata;
    }

    public static Metadata getTransientMetadata(@NonNull Person person) {
        return person.getContextManager().getContext(MetadataContext.class).transientMetadata;
    }

    public abstract ChatColor getNameColor();

    public abstract void setNameColor(ChatColor color);

    public abstract ChatColor getChatColor();

    public abstract void setChatColor(ChatColor color);

    public abstract boolean isIgnoringChannel(String name);

    public abstract void setIgnoringChannel(String name, boolean value);

    public abstract boolean isIgnoring(UUID id);

    public abstract void setIgnoring(UUID id, boolean value);

    public abstract boolean isCommandSpy();

    public abstract void setCommandSpy(boolean value);

    public abstract boolean isSocialSpy();

    public abstract void setSocialSpy(boolean value);

    public abstract boolean isSilent();

    public abstract void setSilent(boolean value);

    public abstract String getNickname();

    public abstract void setNickname(String nickname);

    @Override
    public JsonObject serializeJson() {
        JsonObject serializedJson = new JsonObject();

        serializedJson.add("persistent", new JsonParser().parse(AdvanciusBungee.GSON.toJson(persistentMetadata.getInternal())));
        serializedJson.add("transient", new JsonParser().parse(AdvanciusBungee.GSON.toJson(transientMetadata.getInternal())));

        return serializedJson;
    }

    @Override
    public final String getName() {
        return "metadata";
    }
}
