package net.advancius.channel.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.channel.Channel;
import net.advancius.channel.ChannelFormatter;
import net.advancius.channel.message.event.MessageFormatEvent;
import net.advancius.channel.message.event.MessagePostSendEvent;
import net.advancius.channel.message.event.MessagePreSendEvent;
import net.advancius.person.Person;
import net.advancius.person.context.ConnectionContext;
import net.md_5.bungee.api.chat.TextComponent;

@Data
@AllArgsConstructor
public class ChannelMessage {

    private final Person sender;
    private final Person reader;
    private final Channel channel;

    private String message;

    public void send() {
        if (!channel.getGuard().canPersonSend(sender)) return;
        if (!sender.equals(reader) && !channel.getGuard().canPersonRead(reader)) return;

        MessagePreSendEvent messagePreSendEvent = AdvanciusBungee.getInstance().getEventManager().generateEvent(MessagePreSendEvent.class, this);
        AdvanciusBungee.getInstance().getEventManager().executeEvent(messagePreSendEvent);

        if (messagePreSendEvent.isCancelled()) return;

        MessageFormatEvent formatEvent = AdvanciusBungee.getInstance().getEventManager().generateEvent(MessageFormatEvent.class, this, channel.getDefaultFormatter());
        AdvanciusBungee.getInstance().getEventManager().executeEvent(formatEvent);

        ChannelFormatter formatter = formatEvent.getFormatter();
        TextComponent formattedMessage = formatter.format(this);

        ConnectionContext.sendMessage(reader, formattedMessage);

        MessagePostSendEvent messagePostSendEvent = AdvanciusBungee.getInstance().getEventManager().generateEvent(MessagePostSendEvent.class, this, formattedMessage);
        AdvanciusBungee.getInstance().getEventManager().executeEvent(messagePostSendEvent);
    }
}
