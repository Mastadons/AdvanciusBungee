package net.advancius.channel.configured.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.advancius.channel.message.ChannelMessage;
import net.advancius.event.AbstractEvent;
import net.advancius.placeholder.PlaceholderComponent;

@AllArgsConstructor
public class ConfiguredFormatEvent extends AbstractEvent {

    @Getter
    private ChannelMessage message;
    @Getter
    private PlaceholderComponent component;
    @Getter
    private final boolean cancellable = false;

    @Override
    public void eventCompleted() {
    }

    @Override
    public void eventCancelled() {
    }
}
