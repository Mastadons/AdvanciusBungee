package net.advancius.person.event;

import lombok.Getter;
import net.advancius.AdvanciusLogger;
import net.advancius.person.Person;
import net.advancius.person.context.ConnectionContext;

public class PersonLoadEvent extends PersonEvent {

    @Getter
    private final boolean cancellable = false;

    public PersonLoadEvent(Person person) {
        super(person);
    }

    @Override
    public void eventCompleted() {
        person.getContextManager().loadContexts();

        ConnectionContext connectionContext = person.getContextManager().getContext(ConnectionContext.class);

        AdvanciusLogger.info("Person " + connectionContext.getConnectionName() + " has loaded!");
    }

    @Override
    public void eventCancelled() {
    }
}
