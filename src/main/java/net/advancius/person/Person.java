package net.advancius.person;

import net.advancius.person.context.ContextManager;

import java.util.UUID;

public interface Person {

    UUID getId();

    ContextManager getContextManager();

    default String getReducedId() { return getId().toString().replace("-", ""); }
}
