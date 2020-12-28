package net.advancius;

import lombok.Data;
import lombok.Getter;
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
public class AdvanciusLang {

    @Getter private static AdvanciusLang instance;

    @Getter private static Yaml configurationYaml;
    @Getter private static File configurationFile;

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = -5)
    public static void load() throws FileNotFoundException {
        AdvanciusLogger.info("Loading language...");

        configurationYaml = new Yaml(new CustomClassLoaderConstructor(AdvanciusLang.class.getClassLoader()));
        configurationYaml.setBeanAccess(BeanAccess.FIELD);
        configurationFile = FileManager.getServerFile("language.yml", "language.yml");
        FileReader configurationReader = new FileReader(configurationFile);

        instance = configurationYaml.loadAs(configurationReader, AdvanciusLang.class);
        AdvanciusLogger.info("Loaded language!");
    }

    public String reload;

    public String info;

    public String silentJoin;
    public String silentQuit;
    public String silentMove;

    public String join;
    public String quit;
    public String move;
    public String firstJoin;

    public String channelChange;
    public String channelAlreadyIn;

    public String unknownServer;
    public String unknownColor;
    public String chatColorChanged;
    public String nameColorChanged;

    public String unallowedColor;

    public String incorrectSyntax;

    public String directMessageSender;
    public String directMessageReader;

    public String unknownPlayer;
    public String unknownChannel;
    public String broadcast;
    public String chatCleared;
    public String chatLocked;
    public String chatUnlocked;
    public String cannotChatLocked;

    public String channelIgnored;
    public String channelUnignored;

    public String personIgnored;
    public String personUnignored;

    public String commandSpyToggle;
    public String socialSpyToggle;

    public String commandSpy;
    public String socialSpy;

    public String silentToggle;

    public String nicknameReset;
    public String nicknameChanged;

    public String nicknameMin;
    public String nicknameMax;

    public String cannotIgnore;

    public String mentioned;

    public String noRecentSender;
    public String noRecentReader;

    public String staffListHeader;
    public String staffListFooter;
    public String staffListLine;

    public String cannotMessageSelf;

    public String eightBallFormat;
    public List<String> eightBallResponses;
    public String eightBallChannel;

    public String eightBallIncorrectChannel;
    public String commandCooldown;

    public String cannotChatMuted;

    public String crossServer;
}
