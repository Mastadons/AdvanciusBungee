package net.advancius.channel.event;

import lombok.Data;
import net.advancius.channel.Channel;
import net.advancius.channel.ChannelManager;
import net.advancius.event.AbstractEvent;

@Data
public abstract class ChannelEvent extends AbstractEvent {

    private final Channel channel;
    private final ChannelManager channelManager;
}
