package net.advancius.integration;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.integration.discord.DiscordIntegration;

@Data
@FlagManager.FlaggedClass
public class IntegrationManager {

    private static IntegrationManager instance = new IntegrationManager();

    private DiscordIntegration discordIntegration;

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 40)
    private static void loadIntegrationManager() throws Exception {
        AdvanciusBungee.getInstance().setIntegrationManager(instance);

        DiscordIntegration discordIntegration = new DiscordIntegration();
        discordIntegration.load();
        instance.discordIntegration = discordIntegration;
    }
}
