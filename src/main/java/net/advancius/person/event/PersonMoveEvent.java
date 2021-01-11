package net.advancius.person.event;

import lombok.Getter;
import net.advancius.person.Person;

public class PersonMoveEvent extends PersonEvent {

    @Getter
    private boolean cancellable = false;

    public PersonMoveEvent(Person person) {
        super(person);
    }

    @Override
    public void eventCompleted() {
    }

    @Override
    public void eventCancelled() {
    }
}
