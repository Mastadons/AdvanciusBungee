package net.advancius.event;

import lombok.Data;
import net.advancius.utils.Reflection;

import java.lang.reflect.Method;

@Data
public class EventListenerMethod {

    private final EventListener listener;
    private final Method method;

    public void executeMethod(Event event) {
        Reflection.runMethod(method, listener, event);
    }

    public <T extends Event> Class<T> getEventClass() {
        return (Class<T>) method.getParameterTypes()[0];
    }

    public int getPriority() {
        return method.getAnnotation(EventHandler.class).value();
    }
}
