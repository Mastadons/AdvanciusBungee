package net.advancius.channel.event;

import lombok.Data;
import net.advancius.channel.Channel;
import net.advancius.channel.ChannelManager;
import net.advancius.event.AbstractEvent;

@Data
public abstract class ChannelEvent extends AbstractEvent {

    protected final Channel channel;
    protected final ChannelManager channelManager;
}
