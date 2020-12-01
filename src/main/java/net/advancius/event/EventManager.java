package net.advancius.event;

import net.advancius.AdvanciusBungee;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.utils.Reflection;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@FlagManager.FlaggedClass
public class EventManager {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 20)
    private static void eventManager() {
        AdvanciusBungee.getInstance().setEventManager(new EventManager());
    }

    private final List<Event> eventList = new ArrayList<>();
    private final List<EventListener> eventListenerList = new ArrayList<>();

    public <T extends Event> T generateEvent(Class<T> eventClass, Object... parameters) {
        Constructor<T> constructor = ConstructorUtils.getMatchingAccessibleConstructor(eventClass, Reflection.getClasses(parameters));
        T event = Reflection.runConstructor(constructor, parameters);

        return event;
    }

    public boolean executeEvent(final Event event) {
        List<EventListenerMethod> listenerMethodList = new ArrayList<>();
        eventListenerList.forEach(listener -> listenerMethodList.addAll(listener.getListenerMethods()));

        listenerMethodList.sort(Comparator.comparingInt(EventListenerMethod::getPriority));
        for (EventListenerMethod listenerMethod : listenerMethodList) {
            if (!listenerMethod.getEventClass().equals(event.getClass())) continue;
            if (event.isCancelled() && event.isCancellable()) break;
            listenerMethod.executeMethod(event);
        }

        if (event.isCancelled() && event.isCancellable()) {
            event.eventCancelled();
            return false;
        }
        event.setCompleted(true);
        event.eventCompleted();
        return true;
    }

    public void registerListener(EventListener listener) {
        eventListenerList.add(listener);
    }

    public void unregisterListener(EventListener listener) {
        eventListenerList.remove(listener);
    }
}
