package net.advancius.channel.message.event;

import lombok.Getter;
import net.advancius.channel.message.ChannelMessage;
import net.md_5.bungee.api.chat.TextComponent;

public class MessagePostSendEvent extends ChannelMessageEvent {

    @Getter
    private final TextComponent formattedMessage;
    @Getter
    private final boolean cancellable = false;

    public MessagePostSendEvent(ChannelMessage message, TextComponent formattedMessage) {
        super(message);
        this.formattedMessage = formattedMessage;
    }

    @Override
    public void eventCompleted() {
    }

    @Override
    public void eventCancelled() {
    }
}
