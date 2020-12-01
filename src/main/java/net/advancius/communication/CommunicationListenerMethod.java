package net.advancius.communication;

import lombok.Data;
import net.advancius.communication.client.Client;
import net.advancius.utils.Reflection;

import java.lang.reflect.Method;

@Data
public class CommunicationListenerMethod {

    private final CommunicationListener listener;
    private final Method method;

    public void executeMethod(Client client, CommunicationPacket packet) {
        Reflection.runMethod(method, listener, client, packet);
    }

    public int getCode() {
        return method.getAnnotation(CommunicationHandler.class).code();
    }
}
