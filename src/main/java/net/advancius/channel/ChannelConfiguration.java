package net.advancius.channel;

import lombok.Data;
import lombok.Getter;
import net.advancius.AdvanciusLogger;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.command.CommandDescription;
import net.advancius.file.FileManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@FlagManager.FlaggedClass
@Data
public class ChannelConfiguration {

    @Getter private static ChannelConfiguration instance;

    @Getter private static Yaml configurationYaml;
    @Getter private static File configurationFile;

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 0)
    public static void load() throws FileNotFoundException {
        AdvanciusLogger.info("Loading channel configuration...");

        configurationYaml = new Yaml(new CustomClassLoaderConstructor(ChannelConfiguration.class.getClassLoader()));
        configurationYaml.setBeanAccess(BeanAccess.FIELD);

        configurationFile = FileManager.getServerFile("channels.yml", "channels.yml");
        FileReader configurationReader = new FileReader(configurationFile);

        instance = configurationYaml.loadAs(configurationReader, ChannelConfiguration.class);
        AdvanciusLogger.info("Loaded channel configuration!");
    }

    private String defaultChannel;

    private List<ConfiguredChannel> configuredChannels;
}
