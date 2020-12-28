package net.advancius.person.event;

import lombok.Getter;
import net.advancius.AdvanciusLogger;
import net.advancius.person.Person;
import net.advancius.player.context.BungeecordContext;

public class PersonSaveEvent extends PersonEvent {

    @Getter
    private boolean cancellable = false;

    public PersonSaveEvent(Person person) {
        super(person);
    }

    @Override
    public void eventCompleted() {
        BungeecordContext bungeecordContext = person.getContextManager().getContext(BungeecordContext.class);

        person.getContextManager().saveContexts();
        AdvanciusLogger.info("Person " + bungeecordContext.getProxiedPlayer().getName() + " has saved!");
    }

    @Override
    public void eventCancelled() {}
}
