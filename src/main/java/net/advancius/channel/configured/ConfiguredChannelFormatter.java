package net.advancius.channel.configured;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.channel.ChannelFormatter;
import net.advancius.channel.configured.event.ConfiguredFormatEvent;
import net.advancius.channel.message.ChannelMessage;
import net.advancius.placeholder.PlaceholderComponent;
import net.md_5.bungee.api.chat.TextComponent;

@Data
public class ConfiguredChannelFormatter implements ChannelFormatter {

    private String senderFormat;
    private String readerFormat;

    @Override
    public TextComponent format(ChannelMessage message) {
        String format = readerFormat;
        if (message.getSender().equals(message.getReader())) format = senderFormat;

        PlaceholderComponent component = new PlaceholderComponent(format);

        ConfiguredFormatEvent event = AdvanciusBungee.getInstance().getEventManager().generateEvent(ConfiguredFormatEvent.class, message, component);
        AdvanciusBungee.getInstance().getEventManager().executeEvent(event);

        component = event.getComponent();
        component.replace("sender", message.getSender());
        component.replace("reader", message.getReader());

        component.translateColor();
        component.replace("message", message.getMessage());

        return new TextComponent(component.toTextComponent());
    }
}
