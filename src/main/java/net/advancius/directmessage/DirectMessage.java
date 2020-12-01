package net.advancius.directmessage;

import lombok.Data;
import net.advancius.person.Person;

@Data
public class DirectMessage {

    private final Person sender;
    private final Person reader;

    private final String message;

    public void sendMessage() {
        DirectMessenger.sendDirectMessage(this);
    }
}
