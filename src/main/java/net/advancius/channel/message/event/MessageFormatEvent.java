package net.advancius.channel.message.event;

import lombok.Getter;
import net.advancius.channel.ChannelFormatter;
import net.advancius.channel.message.ChannelMessage;

public class MessageFormatEvent extends ChannelMessageEvent {

    @Getter
    private ChannelFormatter formatter;
    @Getter
    private final boolean cancellable = false;

    public MessageFormatEvent(ChannelMessage message, ChannelFormatter formatter) {
        super(message);
        this.formatter = formatter;
    }

    @Override
    public void eventCompleted() {
    }

    @Override
    public void eventCancelled() {
    }
}
