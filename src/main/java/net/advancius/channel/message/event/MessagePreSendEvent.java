package net.advancius.channel.message.event;

import lombok.Getter;
import net.advancius.channel.message.ChannelMessage;

public class MessagePreSendEvent extends ChannelMessageEvent {

    @Getter
    private final boolean cancellable = true;

    public MessagePreSendEvent(ChannelMessage message) {
        super(message);
    }

    @Override
    public void eventCompleted() {
    }

    @Override
    public void eventCancelled() {
    }
}
