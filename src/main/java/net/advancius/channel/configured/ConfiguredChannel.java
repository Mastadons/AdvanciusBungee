package net.advancius.channel.configured;

import lombok.Data;
import net.advancius.channel.Channel;
import net.advancius.channel.ChannelFormatter;
import net.advancius.channel.ChannelGuard;
import net.advancius.utils.Metadata;

import java.util.Map;
import java.util.UUID;

@Data
public class ConfiguredChannel implements Channel {

    private final UUID id = UUID.randomUUID();

    private String name;
    private ConfiguredChannelGuard guard;
    private ConfiguredChannelFormatter defaultFormatter;
    private Metadata metadata = new Metadata();
}
