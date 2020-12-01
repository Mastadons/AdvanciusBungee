package net.advancius.communication;

import net.advancius.communication.client.Client;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public interface CommunicationListener {

    default Set<CommunicationListenerMethod> getListenerMethods(int code) {
        Set<CommunicationListenerMethod> listenerMethodSet = new HashSet<>();
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(CommunicationHandler.class)) continue;
            if (method.getParameterCount() != 2) continue;
            if (method.getParameterTypes()[0] != Client.class) continue;
            if (!CommunicationPacket.class.isAssignableFrom(method.getParameterTypes()[1])) continue;
            if (method.getAnnotation(CommunicationHandler.class).code() != code) continue;

            CommunicationListenerMethod listenerMethod = new CommunicationListenerMethod(this, method);
            listenerMethodSet.add(listenerMethod);
        }
        return listenerMethodSet;
    }
}