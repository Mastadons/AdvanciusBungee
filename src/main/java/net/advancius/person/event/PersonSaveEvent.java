package net.advancius.person.event;

import lombok.Getter;
import net.advancius.person.Person;

public class PersonSaveEvent extends PersonEvent {

    @Getter
    private boolean cancellable = false;

    public PersonSaveEvent(Person person) {
        super(person);
    }

    @Override
    public void eventCompleted() {
        person.getContextManager().saveContexts();
    }

    @Override
    public void eventCancelled() {
    }
}
