package net.advancius.communication;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLogger;
import net.advancius.communication.exception.PacketListenerException;
import net.advancius.communication.exception.UnhandledPacketException;
import net.advancius.communication.identifier.Identifier;
import net.advancius.communication.packet.PacketHandlerMethod;
import net.advancius.communication.packet.PacketListener;
import net.advancius.communication.packet.Packet;
import net.advancius.communication.servlet.EventsServlet;
import net.advancius.communication.servlet.PacketServlet;
import net.advancius.communication.session.SessionManager;
import net.advancius.file.FileManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Data
@FlagManager.FlaggedClass
public class CommunicationManager {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 5)
    private static void loadChannelManager() throws Exception {
        AdvanciusLogger.info("Loading ChannelManager");
        CommunicationManager instance = new CommunicationManager();
        instance.startCommunicationServer();

        AdvanciusBungee.getInstance().setCommunicationManager(instance);
    }

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_SAVE, priority = 100)
    private static void saveChannelManager() throws Exception {
        AdvanciusLogger.info("Saving ChannelManager");
        CommunicationManager instance = AdvanciusBungee.getInstance().getCommunicationManager();
        instance.stopCommunicationServer();
    }

    private final SessionManager sessionManager = new SessionManager();

    private Server server;

    private final List<PacketListener> listenerList = new ArrayList<>();

    public void startCommunicationServer() throws Exception {
        QueuedThreadPool threadPool = new QueuedThreadPool(100, 10);
        server = new Server(threadPool);

        if (CommunicationConfiguration.getInstance().encryption) {

            HttpConfiguration configuration = new HttpConfiguration();
            configuration.addCustomizer(new SecureRequestCustomizer());

            SslContextFactory contextFactory = new SslContextFactory();
            contextFactory.setKeyStorePath(FileManager.getServerFile(CommunicationConfiguration.getInstance().keystorePath).getPath());
            contextFactory.setKeyStorePassword(CommunicationConfiguration.getInstance().keystorePassword);

            ServerConnector secureConnector = new ServerConnector(server, new SslConnectionFactory(contextFactory, "http/1.1"), new HttpConnectionFactory(configuration));
            secureConnector.setPort(CommunicationConfiguration.getInstance().serverPort);
            secureConnector.setIdleTimeout(CommunicationConfiguration.getInstance().idleTimeout);

            server.setConnectors(new Connector[]{secureConnector});
        }
        else {
            ServerConnector serverConnector = new ServerConnector(server);
            serverConnector.setPort(CommunicationConfiguration.getInstance().serverPort);
            serverConnector.setIdleTimeout(CommunicationConfiguration.getInstance().idleTimeout);
            server.setConnectors(new Connector[] { serverConnector });
        }

        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(EventsServlet.class, "/events");
        handler.addServletWithMapping(PacketServlet.class, "/packet");

        server.setHandler(handler);
        server.start();
    }

    public void stopCommunicationServer() throws Exception {
        sessionManager.terminateSessions();

        server.stop();
    }

    public boolean isAuthentic(String authenticationToken) {
        if (authenticationToken == null) return false;

        return CommunicationConfiguration.getInstance().authenticationTokens.contains(authenticationToken);
    }

    public void handlePacket(Identifier identifier, Packet packet) throws UnhandledPacketException, PacketListenerException {
        AdvanciusLogger.log(Level.INFO, "Client [%s] packet received: %s", identifier, packet.getType());
        for (PacketListener listener : listenerList) {
            PacketHandlerMethod handlerMethod = listener.getHandlerMethod(packet.getType());
            if (handlerMethod == null) continue;

            try {
                handlerMethod.executeMethod(identifier, packet);
                return;
            } catch (Exception exception) {
                throw new PacketListenerException(listener, handlerMethod, packet, exception);
            }
        }
        throw new UnhandledPacketException(packet);
    }

    public void registerListener(PacketListener listener) {
        listenerList.add(listener);
    }

    public void unregisterListener(PacketListener listener) {
        listenerList.remove(listener);
    }
}
