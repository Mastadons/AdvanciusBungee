package net.advancius.listener;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.event.Event;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.ConnectionContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.person.context.PermissionContext;
import net.advancius.person.event.PersonJoinEvent;
import net.advancius.person.event.PersonMoveEvent;
import net.advancius.person.event.PersonQuitEvent;
import net.advancius.placeholder.PlaceholderComponent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

@FlagManager.FlaggedClass
public class DefaultBungeecordListener implements Listener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 30)
    private static void bungeecordListener() {
        ProxyServer.getInstance().getPluginManager().registerListener(AdvanciusBungee.getInstance(), new DefaultBungeecordListener());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPersonJoin(PostLoginEvent event) {
        Person person = AdvanciusBungee.getInstance().getPersonManager().loadPerson(event.getPlayer().getUniqueId());

        Event joinEvent = AdvanciusBungee.getInstance().getEventManager().generateEvent(PersonJoinEvent.class, person);
        AdvanciusBungee.getInstance().getEventManager().executeEvent(joinEvent);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPersonQuit(PlayerDisconnectEvent event) {
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(event.getPlayer().getUniqueId());

        Event quitEvent = AdvanciusBungee.getInstance().getEventManager().generateEvent(PersonQuitEvent.class, person);
        AdvanciusBungee.getInstance().getEventManager().executeEvent(quitEvent);

        AdvanciusBungee.getInstance().getPersonManager().savePerson(person.getId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPersonMove(ServerSwitchEvent event) {
        Person person = AdvanciusBungee.getInstance().getPersonManager().getPerson(event.getPlayer().getUniqueId());

        Event moveEvent = AdvanciusBungee.getInstance().getEventManager().generateEvent(PersonMoveEvent.class, person);
        AdvanciusBungee.getInstance().getEventManager().executeEvent(moveEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPersonCommand(ChatEvent event) {
        if (!event.isCommand() || event.isCancelled()) return;
        if (event.getMessage().length() == 1) return;

        Person person = AdvanciusBungee.getInstance().getPersonManager().getPersonUnsafe(event.getSender());
        PermissionContext permissionContext0 = person.getContextManager().getContext(PermissionContext.class);

        if (permissionContext0.isCommandSpyExempt()) return;

        String commandName = event.getMessage().substring(1).split(" ")[0];
        for (String ignoredCommand : AdvanciusConfiguration.getInstance().commandSpyIgnore)
            if (commandName.equalsIgnoreCase(ignoredCommand)) return;

        for (Person onlinePerson : AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons()) {
            PermissionContext permissionContext = onlinePerson.getContextManager().getContext(PermissionContext.class);
            MetadataContext metadataContext = onlinePerson.getContextManager().getContext(MetadataContext.class);

            if (onlinePerson.equals(person)) continue;
            if (!permissionContext.hasPermission(AdvanciusBungee.getInstance().getCommandManager().getDescription("commandspy").getPermission()))
                continue;
            if (!metadataContext.isCommandSpy()) continue;

            PlaceholderComponent placeholderComponent = new PlaceholderComponent(AdvanciusLang.getInstance().commandSpy);
            placeholderComponent.replace("command", event.getMessage());
            placeholderComponent.replace("person", person);
            placeholderComponent.translateColor();
            placeholderComponent.send(onlinePerson);
        }
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String partial = event.getCursor().toLowerCase();

        int lastSpaceIndex = partial.lastIndexOf(' ');
        if (lastSpaceIndex >= 0) partial = partial.substring(lastSpaceIndex + 1);

        for (Person onlinePerson : AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons()) {
            ConnectionContext connectionContext = onlinePerson.getContextManager().getContext(ConnectionContext.class);
            if (connectionContext.getConnectionName().toLowerCase().startsWith(partial.toLowerCase())) {
                event.getSuggestions().add(connectionContext.getConnectionName());
            }
        }
    }
}
