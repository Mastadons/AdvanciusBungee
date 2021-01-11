package net.advancius.communication.session;

import net.advancius.communication.identifier.Identifier;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private final List<Session> sessionList = new ArrayList<>();

    public void registerSession(Identifier identifier, org.eclipse.jetty.websocket.api.Session session) {
        sessionList.add(new Session(identifier, session));
    }

    public void registerSession(Session session) {
        sessionList.add(session);
    }

    public void unregisterSession(Identifier identifier) {
        sessionList.removeIf(session -> session.getIdentifier().equals(identifier));
    }

    public Session getSession(ServerInfo serverInfo) {
        return sessionList.stream()
                .filter(session -> session.getIdentifier().isMinecraft() && session.getIdentifier().getName().equalsIgnoreCase(serverInfo.getName()))
                .findFirst()
                .orElse(null);
    }

    public void terminateSessions() {
        new ArrayList<>(sessionList).forEach(session -> {
            try {
                session.getSession().disconnect();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }
}
