package net.advancius.channel;

import java.util.UUID;

public interface Channel {

    UUID getId();

    ChannelGuard getGuard();
    ChannelFormatter getDefaultFormatter();
}
