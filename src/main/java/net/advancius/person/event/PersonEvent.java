package net.advancius.person.event;

import lombok.Data;
import net.advancius.event.AbstractEvent;
import net.advancius.person.Person;

@Data
public abstract class PersonEvent extends AbstractEvent {

    protected final Person person;
}
