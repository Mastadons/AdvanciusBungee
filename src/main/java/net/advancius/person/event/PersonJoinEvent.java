package net.advancius.person.event;

import lombok.Getter;
import net.advancius.person.Person;

public class PersonJoinEvent extends PersonEvent {

    @Getter
    private boolean cancellable = false;

    public PersonJoinEvent(Person person) {
        super(person);
    }

    @Override
    public void eventCompleted() {
    }

    @Override
    public void eventCancelled() {
    }
}
