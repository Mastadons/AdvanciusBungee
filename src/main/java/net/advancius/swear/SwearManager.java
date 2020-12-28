package net.advancius.swear;

import net.advancius.AdvanciusBungee;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.channel.message.event.MessageFormatEvent;
import net.advancius.event.EventHandler;
import net.advancius.event.EventListener;
import net.advancius.file.FileManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.context.PermissionContext;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@FlagManager.FlaggedClass
public class SwearManager implements EventListener {

    private final List<String> swearList = new ArrayList<>();

    @FlagManager.FlaggedMethod(priority = 25, flag = DefinedFlag.PLUGIN_LOAD)
    public static void swearManager() throws FileNotFoundException {
        SwearManager instance = new SwearManager();

        File swearsFile = FileManager.getServerFile("swears.txt", "swears.txt");
        Scanner scanner = new Scanner(new FileReader(swearsFile));
        while (scanner.hasNextLine()) instance.swearList.add(scanner.nextLine());

        AdvanciusBungee.getInstance().getEventManager().registerListener(instance);
        AdvanciusBungee.getInstance().setSwearManager(instance);
    }

    @EventHandler(Integer.MIN_VALUE)
    public void onMessageFormat(MessageFormatEvent event) {
        if (!(event.getMessage().getChannel() instanceof ConfiguredChannel)) return;

        ConfiguredChannel channel = (ConfiguredChannel) event.getMessage().getChannel();
        if (channel.getMetadata().hasMetadata("allowSwears", true)) return;

        PermissionContext permissionContext = event.getMessage().getReader().getContextManager().getContext(PermissionContext.class);
        event.getMessage().setMessage(replaceSwears(event.getMessage().getMessage(), permissionContext.hasPermission("advancius.swearbypass")));
    }

    public String replaceSwears(String message, boolean show) {
        String[] components = message.split(" ");
        for (int i = 0; i < components.length; i++) {
            for (String swear : swearList) {
                if (!components[i].equalsIgnoreCase(swear)) continue;
                if (show) components[i] = StringUtils.replaceIgnoreCase(components[i], swear, ChatColor.RED + ""   + ChatColor.UNDERLINE + swear + "" + ChatColor.RESET);
                else      components[i] = StringUtils.replaceIgnoreCase(components[i], swear, ChatColor.RED + StringUtils.repeat('*', components[i].length()) + "" + ChatColor.RESET);
                break;
            }
        }
        return String.join(" ", components);
    }
}
