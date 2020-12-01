package net.advancius.channel.message.event;

import lombok.Data;
import net.advancius.channel.message.ChannelMessage;
import net.advancius.event.AbstractEvent;

@Data
public abstract class ChannelMessageEvent extends AbstractEvent {

    private final ChannelMessage message;
}
