package net.advancius.communication.servlet;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLogger;
import net.advancius.communication.identifier.Identifier;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import re.org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import re.org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.util.logging.Level;

public class EventsServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(EventsWebsocket.class);
    }

    public static class EventsWebsocket extends WebSocketAdapter {

        private volatile Session session;

        private Identifier identifier;

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            AdvanciusBungee.getInstance().getCommunicationManager().getSessionManager().unregisterSession(identifier);
            AdvanciusLogger.log(Level.INFO, "Client [%s] disconnected from events servlet: %s", identifier, reason);
            this.session = null;
        }

        @Override
        public void onWebSocketConnect(Session session) {
            this.session = session;

            String authenticationToken = session.getUpgradeRequest().getHeader("Advancius-Authentication-Token");

            try {
                identifier = Identifier.fromString(session.getUpgradeRequest().getHeader("Advancius-Identifier"));
            } catch (Exception exception) {
                AdvanciusLogger.warn("Unidentified client attempted to connect to events servlet.");
                session.close(400, "Invalid or missing identifier.");
                return;
            }

            if (!AdvanciusBungee.getInstance().getCommunicationManager().isAuthentic(authenticationToken)) {
                AdvanciusLogger.warn("Unauthorized client attempted to connect to events servlet.");
                session.close(400, "Invalid or missing authentication token.");
                return;
            }

            AdvanciusBungee.getInstance().getCommunicationManager().getSessionManager().registerSession(identifier, session);
            AdvanciusLogger.log(Level.INFO, "Client [%s] connected to events servlet", identifier);
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            AdvanciusLogger.log(Level.INFO, "Client [%s] disconnected threw %s to events servlet: %s", identifier, cause.getClass().getSimpleName(), cause.getMessage());
        }

        @Override
        public void onWebSocketText(String message) {}

        @Override
        public void onWebSocketBinary(byte[] bytes, int i, int i1) {}
    }
}
