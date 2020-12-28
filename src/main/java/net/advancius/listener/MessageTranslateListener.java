package net.advancius.listener;

import net.advancius.AdvanciusBungee;
import net.advancius.channel.message.event.MessageFormatEvent;
import net.advancius.event.EventHandler;
import net.advancius.event.EventListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.context.PermissionContext;

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
        if (PermissionContext.hasPermission(event.getMessage().getSender(), "advancius.translatecolor"))
            event.getMessage().setMessage(translate(event.getMessage().getMessage(), COLOR_CODES));

        if (PermissionContext.hasPermission(event.getMessage().getSender(), "advancius.translateformat"))
            event.getMessage().setMessage(translate(event.getMessage().getMessage(), FORMAT_CODES));
    }

    private static String translate(String message, char[] codes) {
        for (char code : codes) message = message.replace("&" + code, "§" + code);
        return message;
    }
}
