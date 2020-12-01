package net.advancius.event;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public interface EventListener {

    default Set<EventListenerMethod> getListenerMethods() {
        Set<EventListenerMethod> listenerMethodSet = new HashSet<>();
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventHandler.class)) continue;
            if (method.getParameterCount() != 1) continue;
            if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) continue;

            EventListenerMethod listenerMethod = new EventListenerMethod(this, method);
            listenerMethodSet.add(listenerMethod);
        }
        return listenerMethodSet;
    }
}
