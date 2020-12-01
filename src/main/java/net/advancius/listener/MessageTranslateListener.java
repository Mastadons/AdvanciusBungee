package net.advancius.listener;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.channel.message.event.MessageFormatEvent;
import net.advancius.event.EventHandler;
import net.advancius.event.EventListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;
import net.advancius.person.context.ChannelContext;
import net.advancius.person.context.ContextManager;
import net.advancius.person.context.MetadataContext;
import net.advancius.person.context.PermissionContext;
import net.advancius.person.event.PersonJoinEvent;
import net.advancius.person.event.PersonLoadEvent;
import net.advancius.person.event.PersonMoveEvent;
import net.advancius.person.event.PersonQuitEvent;
import net.advancius.person.event.PersonSaveEvent;
import net.advancius.placeholder.PlaceholderComponent;
import net.md_5.bungee.api.ProxyServer;

@FlagManager.FlaggedClass
public class MessageTranslateListener implements EventListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 50)
    private static void eventListener() {
        AdvanciusBungee.getInstance().getEventManager().registerListener(new MessageTranslateListener());
    }

    public static char[] COLOR_CODES = "0123456789abcdef".toCharArray();
    public static char[] FORMAT_CODES = "klmnor".toCharArray();

    @EventHandler(Integer.MIN_VALUE)
    public void onMessageFormat(MessageFormatEvent event) {
        PermissionContext permissionContext = event.getMessage().getSender().getContextManager().getContext(PermissionContext.class);

        if (permissionContext.hasPermission("advancius.translatecolor"))
            event.getMessage().setMessage(translate(event.getMessage().getMessage(), COLOR_CODES));

        if (permissionContext.hasPermission("advancius.translateformat"))
            event.getMessage().setMessage(translate(event.getMessage().getMessage(), FORMAT_CODES));
    }

    private static String translate(String message, char[] codes) {
        for (char code : codes) message = message.replace("&" + code, "§" + code);
        return message;
    }
}
