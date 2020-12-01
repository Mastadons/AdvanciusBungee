package net.advancius.person.context;

import lombok.Data;
import net.advancius.person.Person;
import net.advancius.utils.Reflection;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Data
public class ContextManager {

    private final Person person;
    private final List<PersonContext> contextList = new ArrayList<>();

    public <T extends PersonContext> T getContext(Class<T> contextClass) {
        return (T) contextList.stream().filter(context -> context.getClass() == contextClass).findFirst().orElse(null);
    }

    public <T extends PersonContext> T getContext(String name) {
        return (T) contextList.stream().filter(context -> context.getName().equals(name)).findFirst().orElse(null);
    }

    public <T extends PersonContext> void addContext(Class<T> contextClass, int priority) {
        if (hasContext(contextClass)) return;

        PersonContext personContext = Reflection.runConstructor(ConstructorUtils.getMatchingAccessibleConstructor(contextClass));
        contextList.add(personContext);

        personContext.setPriority(priority);
        personContext.setPerson(person);
    }

    public <T extends PersonContext> void removeContext(Class<T> contextClass) {
        for (PersonContext personContext : contextList)
            if (personContext.getClass() == contextClass) personContext.setPerson(null);
        contextList.removeIf(context -> context.getClass() == contextClass);
    }

    public <T extends PersonContext> boolean hasContext(Class<T> contextClass) {
        return getContext(contextClass) != null;
    }

    public void loadContexts() {
        Collections.sort(contextList, Comparator.comparingInt(context -> context.getPriority()));
        for (PersonContext personContext : contextList) {
            try {
                personContext.onPersonLoad();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void saveContexts() {
        Collections.sort(contextList, Comparator.comparingInt(context -> context.getPriority()));
        for (PersonContext personContext : contextList) {
            try {
                personContext.onPersonSave();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
