package net.advancius.person.event;

import lombok.Getter;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;

public class PersonJoinEvent extends PersonEvent {

    @Getter private boolean cancellable = true;

    public PersonJoinEvent(Person person) {
        super(person);
    }

    @Override
    public void eventCompleted() {}

    @Override
    public void eventCancelled() {
        BungeecordContext bungeecordContext = person.getContextManager().getContext("bungeecord");
        if (bungeecordContext.getProxiedPlayer().isConnected()) bungeecordContext.getProxiedPlayer().disconnect();
    }
}
