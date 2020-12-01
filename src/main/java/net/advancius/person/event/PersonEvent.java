package net.advancius.person.event;

import lombok.Data;
import net.advancius.person.Person;
import net.advancius.event.AbstractEvent;

@Data
public abstract class PersonEvent extends AbstractEvent {

    protected final Person person;
}
