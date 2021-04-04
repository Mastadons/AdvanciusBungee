package net.advancius;

import lombok.Data;
import lombok.Getter;
import net.advancius.file.FileManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.integration.discord.DiscordIntegrationConfiguration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FlagManager.FlaggedClass
@Data
public class AdvanciusConfiguration {

    @Getter
    private static AdvanciusConfiguration instance;

    @Getter
    private static Yaml configurationYaml;
    @Getter
    private static File configurationFile;

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 0)
    public static void load() throws FileNotFoundException {
        AdvanciusLogger.info("Loading configuration...");

        configurationYaml = new Yaml(new CustomClassLoaderConstructor(AdvanciusConfiguration.class.getClassLoader()));
        configurationYaml.setBeanAccess(BeanAccess.FIELD);
        configurationFile = FileManager.getServerFile("configuration.yml", "configuration.yml");
        FileReader configurationReader = new FileReader(configurationFile);

        instance = configurationYaml.loadAs(configurationReader, AdvanciusConfiguration.class);
        AdvanciusLogger.info("Loaded configuration!");
        FlagManager.runFlaggedMethods("net.advancius", DefinedFlag.POST_CONFIGURATION_LOAD);
    }

    public boolean debugExceptions;
    public List<String> serverProcessing;

    public String defaultChatColor;
    public String defaultNameColor;

    public Map<String, List<String>> colorAliases;

    public List<String> allowedNameColor;
    public List<String> allowedChatColor;

    public List<String> silentMoveServers;
    public List<String> commandSpyIgnore;

    public int maxNicknameLength;
    public int minNicknameLength;

    public String eightBallChannel;

    public long eightBallCooldown;

    public int port;

    public DiscordIntegrationConfiguration discordIntegration = new DiscordIntegrationConfiguration();
}
