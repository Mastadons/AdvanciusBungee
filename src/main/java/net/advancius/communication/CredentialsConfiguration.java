package net.advancius.communication;

import lombok.Data;
import lombok.Getter;
import net.advancius.AdvanciusLogger;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.communication.client.ClientCredentials;
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
import java.util.Map;

@FlagManager.FlaggedClass
public class CredentialsConfiguration {

    @Getter private static CredentialsConfiguration instance;

    @Getter private static Yaml configurationYaml;
    @Getter private static File configurationFile;

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 0)
    public static void load() throws FileNotFoundException {
        AdvanciusLogger.info("Loading credentials configuration...");

        configurationYaml = new Yaml(new CustomClassLoaderConstructor(CredentialsConfiguration.class.getClassLoader()));
        configurationYaml.setBeanAccess(BeanAccess.FIELD);

        configurationFile = FileManager.getServerFile("credentials.yml", "credentials.yml");
        FileReader configurationReader = new FileReader(configurationFile);

        instance = configurationYaml.loadAs(configurationReader, CredentialsConfiguration.class);
        AdvanciusLogger.info("Loaded credentials configuration!");
    }

    public Map<String, ClientCredentials> credentials;
}
