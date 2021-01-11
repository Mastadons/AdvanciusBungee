package net.advancius.person.context;

import lombok.Data;
import net.advancius.channel.configured.ConfiguredChannel;

@Data
public abstract class ChannelContext extends PersonContext {

    public abstract ConfiguredChannel getChannel();

    public abstract void setChannel(ConfiguredChannel channel);

    @Override
    public final String getName() {
        return "channel";
    }
}
