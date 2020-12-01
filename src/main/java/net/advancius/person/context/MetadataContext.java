package net.advancius.person.context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.file.FileManager;
import net.advancius.person.Person;
import net.advancius.utils.ColorUtils;
import net.advancius.utils.Metadata;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class MetadataContext extends PersonContext {

    private Metadata persistentMetadata = new Metadata();
    private Metadata transientMetadata  = new Metadata();

    public static Metadata getPersistentMetadata(Person person) {
        return person.getContextManager().getContext(MetadataContext.class).persistentMetadata;
    }

    public static Metadata getTransientMetadata(Person person) {
        return person.getContextManager().getContext(MetadataContext.class).transientMetadata;
    }

    public ChatColor getNameColor() {
        PermissionContext permissionContext = person.getContextManager().getContext("permissions");
        String permission = AdvanciusBungee.getInstance().getCommandManager().getDescription("namecolor").getPermission();

        if (!permissionContext.hasPermission(permission)) return ColorUtils.getColor(AdvanciusConfiguration.getInstance().defaultNameColor);

        String colorName = persistentMetadata.getMetadata("namecolor");
        return colorName == null ? ColorUtils.getColor(AdvanciusConfiguration.getInstance().defaultNameColor) : ColorUtils.getColor(colorName);
    }

    public void setNameColor(ChatColor color) {
        persistentMetadata.setMetadata("namecolor", color.name());
    }

    public ChatColor getChatColor() {
        PermissionContext permissionContext = person.getContextManager().getContext("permissions");
        String permission = AdvanciusBungee.getInstance().getCommandManager().getDescription("chatcolor").getPermission();

        if (!permissionContext.hasPermission(permission)) return ColorUtils.getColor(AdvanciusConfiguration.getInstance().defaultChatColor);

        String colorName = persistentMetadata.getMetadata("chatcolor");
        return colorName == null ? ColorUtils.getColor(AdvanciusConfiguration.getInstance().defaultChatColor) : ColorUtils.getColor(colorName);
    }

    public void setChatColor(ChatColor color) {
        persistentMetadata.setMetadata("chatcolor", color.name());
    }

    public boolean isIgnoringChannel(String name) {
        List<String> ignoredChannelList = transientMetadata.getMetadata("channelIgnored");
        if (ignoredChannelList == null) {
            transientMetadata.setMetadata("channelIgnored", new ArrayList<String>());
            ignoredChannelList = transientMetadata.getMetadata("channelIgnored");
        }
        return ignoredChannelList.contains(name);
    }

    public void setIgnoringChannel(String name, boolean value) {
        List<String> ignoredChannelList = transientMetadata.getMetadata("channelIgnored");
        if (ignoredChannelList == null) {
            transientMetadata.setMetadata("channelIgnored", new ArrayList<String>());
            ignoredChannelList = transientMetadata.getMetadata("channelIgnored");
        }
        if (value) ignoredChannelList.add(name);
        else ignoredChannelList.remove(name);
    }

    public boolean isIgnoring(UUID id) {
        List<UUID> ignoredList = persistentMetadata.getMetadata("ignored");
        if (ignoredList == null) {
            persistentMetadata.setMetadata("ignored", new ArrayList<String>());
            ignoredList = persistentMetadata.getMetadata("ignored");
        }
        return ignoredList.contains(id);
    }

    public void setIgnoring(UUID id, boolean value) {
        List<UUID> ignoredList = persistentMetadata.getMetadata("ignored");
        if (ignoredList == null) {
            persistentMetadata.setMetadata("ignored", new ArrayList<UUID>());
            ignoredList = persistentMetadata.getMetadata("ignored");
        }
        if (value) ignoredList.add(id);
        else ignoredList.remove(id);
    }

    public boolean isCommandSpy() {
        PermissionContext permissionContext = person.getContextManager().getContext("permissions");
        String permission = AdvanciusBungee.getInstance().getCommandManager().getDescription("commandspy").getPermission();
        if (!permissionContext.hasPermission(permission)) return false;

        return persistentMetadata.hasMetadata("commandspy", true);
    }

    public void setCommandSpy(boolean value) {
        persistentMetadata.setMetadata("commandspy", value);
    }

    public boolean isSocialSpy() {
        PermissionContext permissionContext = person.getContextManager().getContext("permissions");
        String permission = AdvanciusBungee.getInstance().getCommandManager().getDescription("socialspy").getPermission();
        if (!permissionContext.hasPermission(permission)) return false;

        return persistentMetadata.hasMetadata("socialspy", true);
    }

    public void setSocialSpy(boolean value) {
        persistentMetadata.setMetadata("socialspy", value);
    }

    public boolean isSilent() {
        PermissionContext permissionContext = person.getContextManager().getContext("permissions");
        String permission = AdvanciusBungee.getInstance().getCommandManager().getDescription("silent").getPermission();
        if (!permissionContext.hasPermission(permission)) return false;

        return persistentMetadata.hasMetadata("silent", true);
    }

    public void setSilent(boolean value) {
        persistentMetadata.setMetadata("silent", value);
    }

    public String getNickname() {
        BungeecordContext bungeecordContext = person.getContextManager().getContext("bungeecord");

        PermissionContext permissionContext = person.getContextManager().getContext("permissions");
        String permission = AdvanciusBungee.getInstance().getCommandManager().getDescription("nickname").getPermission();
        if (!permissionContext.hasPermission(permission)) return bungeecordContext.getProxiedPlayer().getDisplayName();

        String nickname = persistentMetadata.getMetadata("nickname");
        return nickname == null ? bungeecordContext.getProxiedPlayer().getDisplayName() : nickname;
    }

    public void setNickname(String nickname) {
        persistentMetadata.setMetadata("nickname", nickname);
    }

    @Override
    public JsonObject serializeJson() {
        JsonObject serializedJson = new JsonObject();

        serializedJson.add("persistent", new JsonParser().parse(AdvanciusBungee.GSON.toJson(persistentMetadata.getInternal())));
        serializedJson.add("transient",  new JsonParser().parse(AdvanciusBungee.GSON.toJson( transientMetadata.getInternal())));

        return serializedJson;
    }

    @Override
    public void onPersonLoad() throws IOException {
        FileManager.getServerFile("persons").mkdirs();
        File file = FileManager.getServerFile("persons/" + person.getId() + ".json");
        if (!file.exists()) {
            file.createNewFile();
            return;
        }
        persistentMetadata.deserialize(new JsonParser().parse(new String(Files.readAllBytes(file.toPath()))).getAsJsonObject());
    }

    @Override
    public void onPersonSave() throws IOException {
        FileManager.getServerFile("persons").mkdirs();
        File file = FileManager.getServerFile("persons/" + person.getId() + ".json");
        if (!file.exists()) file.createNewFile();

        Files.write(file.toPath(), persistentMetadata.serialize().toString().getBytes());
    }

    @Override
    public String getName() {
        return "metadata";
    }
}
