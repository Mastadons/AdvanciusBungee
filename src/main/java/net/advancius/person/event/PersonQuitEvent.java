package net.advancius.person.event;

import lombok.Getter;
import net.advancius.person.Person;

public class PersonQuitEvent extends PersonEvent {

    @Getter
    private boolean cancellable = false;

    public PersonQuitEvent(Person person) {
        super(person);
    }

    @Override
    public void eventCompleted() {
    }

    @Override
    public void eventCancelled() {
    }
}
