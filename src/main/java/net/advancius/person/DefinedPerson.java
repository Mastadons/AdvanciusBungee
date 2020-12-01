package net.advancius.person;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.advancius.person.context.ContextManager;
import net.advancius.person.context.PersonContext;
import net.advancius.placeholder.WildcardPlaceholder;

import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefinedPerson implements Person {

    @EqualsAndHashCode.Include
    private final UUID id;

    @ToString.Exclude
    private final ContextManager contextManager = new ContextManager(this);

    @WildcardPlaceholder
    private PersonContext wildcardPlaceholder(String argument) {
        return contextManager.getContext(argument);
    }
}
