package net.advancius.silent;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.event.EventHandler;
import net.advancius.event.EventListener;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.person.context.PermissionContext;
import net.advancius.person.event.PersonJoinEvent;
import net.advancius.person.event.PersonMoveEvent;
import net.advancius.person.event.PersonQuitEvent;
import net.advancius.placeholder.PlaceholderComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

@FlagManager.FlaggedClass
public class SilentListener implements EventListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 50)
    private static void eventListener() {
        AdvanciusBungee.getInstance().getEventManager().registerListener(new SilentListener());
    }

    @EventHandler(5)
    public void onPersonJoin(PersonJoinEvent event) throws IllegalAccessException {
        BungeecordContext bungeecordContext = event.getPerson().getContextManager().getContext(BungeecordContext.class);
        MetadataContext metadata = event.getPerson().getContextManager().getContext("metadata");
        if (metadata.isSilent()) {
            PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().silentJoin);
            component.replace("person", event.getPerson());
            component.translateColor();

            broadcastSilent(component.toTextComponentUnsafe());
        }
    }

    @EventHandler(5)
    public void onPersonQuit(PersonQuitEvent event) {
        MetadataContext metadata = event.getPerson().getContextManager().getContext("metadata");
        if (metadata.isSilent()) {
            PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().silentQuit);
            component.replace("person", event.getPerson());
            component.translateColor();

            broadcastSilent(component.toTextComponentUnsafe());
        }
    }

    @EventHandler(5)
    public void onPersonMove(PersonMoveEvent event) {
        MetadataContext metadata = event.getPerson().getContextManager().getContext("metadata");
        BungeecordContext bungeecordContext = event.getPerson().getContextManager().getContext("bungeecord");

        if ((metadata.isSilent() || AdvanciusConfiguration.getInstance().silentMoveServers.contains(bungeecordContext.getServer().getName())) && metadata.getTransientMetadata().hasMetadata("ending_server")) {
            PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().silentMove);
            component.replace("person", event.getPerson());
            component.replace("server", bungeecordContext.getProxiedPlayer().getServer().getInfo());
            component.translateColor();

            TextComponent textComponent = component.toTextComponentUnsafe();
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + bungeecordContext.getServer().getName()));
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Click to follow to server")));

            broadcastSilent(textComponent);
        }
    }

    private static void broadcastSilent(TextComponent component) {
        for (Person onlinePerson : AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons()) {
            PermissionContext permissionContext = onlinePerson.getContextManager().getContext(PermissionContext.class);

            if (!permissionContext.hasPermission(AdvanciusBungee.getInstance().getCommandManager().getDescription("silent").getPermission())) continue;
            onlinePerson.getContextManager().getContext(BungeecordContext.class).sendMessage(component);
        }
    }
}
