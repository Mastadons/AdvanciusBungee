package net.advancius.person.event;

import lombok.Getter;
import net.advancius.AdvanciusLogger;
import net.advancius.person.Person;
import net.advancius.player.context.BungeecordContext;

public class PersonLoadEvent extends PersonEvent {

    @Getter
    private final boolean cancellable = false;

    public PersonLoadEvent(Person person) {
        super(person);
    }

    @Override
    public void eventCompleted() {
        BungeecordContext bungeecordContext = person.getContextManager().getContext(BungeecordContext.class);

        person.getContextManager().loadContexts();
        AdvanciusLogger.info("Person " + bungeecordContext.getProxiedPlayer().getName() + " has loaded!");
    }

    @Override
    public void eventCancelled() {}
}
