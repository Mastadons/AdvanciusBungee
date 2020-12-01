package net.advancius.channel;

import net.advancius.person.Person;

public interface ChannelGuard {

    boolean canPersonRead(Person person);
    boolean canPersonSend(Person person);
}
