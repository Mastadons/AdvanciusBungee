package net.advancius.channel.message.event;

import lombok.Getter;
import lombok.Setter;
import net.advancius.channel.Channel;
import net.advancius.channel.ChannelManager;
import net.advancius.channel.event.ChannelEvent;
import net.advancius.person.Person;

import java.util.List;


public class MessageGenerateEvent extends ChannelEvent {

    @Getter @Setter private Person sender;
    @Getter @Setter private String message;
    @Getter @Setter private List<Person> readers;
    @Getter @Setter private boolean cancellable = true;

    public MessageGenerateEvent(ChannelManager channelManager, Person sender, Channel channel, String message, List<Person> readers) {
        super(channel, channelManager);
        this.sender = sender;
        this.message = message;
        this.readers = readers;
    }

    @Override
    public void eventCompleted() {}

    @Override
    public void eventCancelled() {}
}
