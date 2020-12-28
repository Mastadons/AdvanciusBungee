package net.advancius.channel.message.event;

import lombok.Getter;
import lombok.Setter;
import net.advancius.channel.Channel;
import net.advancius.channel.ChannelManager;
import net.advancius.channel.configured.ConfiguredChannel;
import net.advancius.channel.event.ChannelEvent;
import net.advancius.channel.message.ChannelMessage;
import net.advancius.person.Person;
import net.advancius.person.context.MetadataContext;

import java.util.ArrayList;
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

    public List<ChannelMessage> generate() {
        List<ChannelMessage> messageList = new ArrayList<>();

        for (Person reader : readers) {
            MetadataContext metadataContext = reader.getContextManager().getContext(MetadataContext.class);
            if (channel instanceof ConfiguredChannel)
                if (metadataContext.isIgnoringChannel(((ConfiguredChannel) channel).getName())) continue;

            if (metadataContext.isIgnoring(sender.getId())) continue;
            if (!channel.getGuard().canPersonSend(sender)) continue;
            if (!sender.equals(reader) && !channel.getGuard().canPersonRead(reader)) continue;

            messageList.add(new ChannelMessage(sender, reader, channel, message));
        }
        return messageList;
    }
}
