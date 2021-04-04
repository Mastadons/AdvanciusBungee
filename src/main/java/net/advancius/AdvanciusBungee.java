package net.advancius;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.advancius.channel.ChannelManager;
import net.advancius.command.CommandManager;
import net.advancius.communication.CommunicationManager;
import net.advancius.event.EventManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.integration.IntegrationManager;
import net.advancius.person.PersonManager;
import net.advancius.statistic.StatisticManager;
import net.advancius.swear.SwearManager;
import net.advancius.utils.ColorUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class AdvanciusBungee extends Plugin {

    public static Gson GSON = new Gson();

    @Getter private static AdvanciusBungee instance;

    @Getter @Setter private SwearManager swearManager;
    @Getter @Setter private CommunicationManager communicationManager;
    @Getter @Setter private EventManager eventManager;
    @Getter @Setter private StatisticManager statisticManager;
    @Getter @Setter private ChannelManager channelManager;
    @Getter @Setter private CommandManager commandManager;
    @Getter @Setter private PersonManager personManager;
    @Getter @Setter private IntegrationManager integrationManager;

    @Override
    public void onEnable() {
        super.onEnable();
        AdvanciusBungee.instance = this;

        FlagManager.runFlaggedMethods("net.advancius", DefinedFlag.PLUGIN_LOAD);
    }

    @Override
    public void onDisable() {
        FlagManager.runFlaggedMethods("net.advancius", DefinedFlag.PLUGIN_SAVE);

        super.onDisable();
        AdvanciusBungee.instance = null;
    }

    public static void broadcastMessage(String message, Object... placeholders) {
        ProxyServer.getInstance().broadcast(ColorUtils.toTextComponent(message, placeholders));
    }
}
