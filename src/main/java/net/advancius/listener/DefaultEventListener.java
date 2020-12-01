package net.advancius.listener;

import litebans.api.Database;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusConfiguration;
import net.advancius.AdvanciusLang;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.channel.message.event.MessageGenerateEvent;
import net.advancius.channel.message.event.MessagePostSendEvent;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;
import net.advancius.person.context.ChannelContext;
import net.advancius.person.context.ContextManager;
import net.advancius.person.context.PermissionContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.person.event.PersonJoinEvent;
import net.advancius.person.event.PersonLoadEvent;
import net.advancius.person.event.PersonMoveEvent;
import net.advancius.person.event.PersonQuitEvent;
import net.advancius.person.event.PersonSaveEvent;
import net.advancius.placeholder.PlaceholderComponent;
import net.advancius.event.EventHandler;
import net.advancius.event.EventListener;
import net.advancius.utils.Commons;
import net.advancius.utils.Metadata;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;

@FlagManager.FlaggedClass
public class DefaultEventListener implements EventListener {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 50)
    private static void eventListener() {
        AdvanciusBungee.getInstance().getEventManager().registerListener(new DefaultEventListener());
    }

    @EventHandler(Integer.MIN_VALUE)
    public void onPersonLoad(PersonLoadEvent event) {
        ContextManager contextManager = event.getPerson().getContextManager();

        contextManager.addContext(BungeecordContext.class, 0);
        contextManager.addContext(PermissionContext.class, 1);
        contextManager.addContext(MetadataContext.class, 2);
        contextManager.addContext(ChannelContext.class, 3);
    }

    @EventHandler(Integer.MAX_VALUE)
    public void onPersonJoin(PersonJoinEvent event) {
        MetadataContext metadata = event.getPerson().getContextManager().getContext("metadata");
        BungeecordContext bungeecord = event.getPerson().getContextManager().getContext("bungeecord");

        if (!metadata.isSilent()) {
            if (!metadata.getPersistentMetadata().hasMetadata("last_username")) {
                PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().getFirstJoin());
                component.replace("person", event.getPerson());
                component.translateColor();

                ProxyServer.getInstance().broadcast(component.toTextComponentUnsafe());
            }
            PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().getJoin());
            component.replace("person", event.getPerson());
            component.translateColor();

            ProxyServer.getInstance().broadcast(component.toTextComponentUnsafe());
        }
        metadata.getPersistentMetadata().setMetadata("last_username", bungeecord.getProxiedPlayer().getName());
    }

    @EventHandler(Integer.MAX_VALUE)
    public void onPersonQuit(PersonQuitEvent event) {
        MetadataContext metadata = event.getPerson().getContextManager().getContext("metadata");
        if (!metadata.isSilent()) {
            PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().getQuit());
            component.replace("person", event.getPerson());
            component.translateColor();

            ProxyServer.getInstance().broadcast(component.toTextComponentUnsafe());
        }
    }

    @EventHandler(Integer.MAX_VALUE)
    public void onPersonMove(PersonMoveEvent event) {
        MetadataContext metadata = event.getPerson().getContextManager().getContext("metadata");
        BungeecordContext bungeecordContext = event.getPerson().getContextManager().getContext("bungeecord");

        if (!metadata.isSilent() && !AdvanciusConfiguration.getInstance().silentMoveServers.contains(bungeecordContext.getServer().getName()) && metadata.getTransientMetadata().hasMetadata("ending_server")) {
            PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().getMove());
            component.replace("person", event.getPerson());
            component.replace("server", bungeecordContext.getProxiedPlayer().getServer().getInfo());
            component.translateColor();

            TextComponent textComponent = component.toTextComponentUnsafe();
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + bungeecordContext.getServer().getName()));
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Click to follow to server")));
            ProxyServer.getInstance().broadcast(textComponent);
        }

        metadata.getTransientMetadata().setMetadata("ending_server", bungeecordContext.getServer().getName());
    }

    @EventHandler(Integer.MAX_VALUE)
    public void onMessagePostSend(MessagePostSendEvent event) {
        if (event.getMessage().getSender().equals(event.getMessage().getReader())) return;
        BungeecordContext bungeecordContext = event.getMessage().getReader().getContextManager().getContext(BungeecordContext.class);

        String message = event.getMessage().getMessage().toLowerCase();
        if (!message.contains(bungeecordContext.getProxiedPlayer().getDisplayName().toLowerCase())) return;

        bungeecordContext.sendTitle("&6", AdvanciusLang.getInstance().mentioned, 0, 30, 0);
    }

    @EventHandler(Integer.MIN_VALUE)
    public void checkPersonMuted(MessageGenerateEvent event) {
        if (Database.get().isPlayerMuted(event.getSender().getId(), null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(Integer.MIN_VALUE)
    public void checkChannelCooldown(MessageGenerateEvent event) {
        if (PermissionContext.hasPermission(event.getSender(), "advancius.cooldownbypass")) return;
        if (!(event.getChannel() instanceof ConfiguredChannel)) return;
        ConfiguredChannel channel = (ConfiguredChannel) event.getChannel();

        if (!channel.getMetadata().hasMetadata("cooldown")) return;

        long cooldown = channel.getMetadata().getMetadata("cooldown", Integer.class);
        long previous = MetadataContext.getTransientMetadata(event.getSender()).getMetadataOr(channel.getName() + "-lastsent", 0L);

        if (Commons.onCooldown(event.getSender(), previous, cooldown, true))
            event.setCancelled(true);
        else MetadataContext.getTransientMetadata(event.getSender()).setMetadata(channel.getName() + "-lastsent", System.currentTimeMillis());
    }

    @EventHandler(Integer.MIN_VALUE)
    public void onArgumentReader(MessageGenerateEvent event) {
        if (!(event.getChannel() instanceof ConfiguredChannel)) return;
        ConfiguredChannel channel = (ConfiguredChannel) event.getChannel();

        if (!channel.getMetadata().hasMetadata("argumentReader", true)) return;
        String[] components = event.getMessage().split(" ");

        Person targetPerson = AdvanciusBungee.getInstance().getPersonManager().getOnlinePerson(components[0]);
        String message = components.length == 1 ? "" : String.join(" ", Arrays.copyOfRange(components, 1, components.length));

        if (targetPerson == null) {
            BungeecordContext.sendMessage(event.getSender(), AdvanciusLang.getInstance().unknownPlayer);
            event.setCancelled(true);
            return;
        }

        event.setReaders(Arrays.asList(event.getSender(), targetPerson));
        event.setMessage(message);
    }
}
