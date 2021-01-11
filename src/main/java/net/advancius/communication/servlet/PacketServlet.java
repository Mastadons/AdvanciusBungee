package net.advancius.communication.servlet;

import com.google.gson.JsonObject;
import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLogger;
import net.advancius.communication.CommunicationCommons;
import net.advancius.communication.exception.PacketListenerException;
import net.advancius.communication.exception.UnhandledPacketException;
import net.advancius.communication.identifier.Identifier;
import net.advancius.communication.packet.Packet;
import net.advancius.utils.Metadata;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;

public class PacketServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authenticationToken = request.getHeader("Advancius-Authentication-Token");

        Identifier identifier;
        try {
            identifier = Identifier.fromString(request.getHeader("Advancius-Identifier"));
        } catch (Exception exception) {
            AdvanciusLogger.warn("Unidentified client attempted to connect to packet servlet.");
            sendError(response, 400, new IllegalStateException("Invalid or missing identifier."));
            return;
        }

        if (!AdvanciusBungee.getInstance().getCommunicationManager().isAuthentic(authenticationToken)) {
            AdvanciusLogger.warn("Unauthorized client attempted to connect to packet servlet.");
            response.setHeader("WWW-Authenticate", "Advancius-Authentication-Token realm=\"Requires authentication token\"");
            sendError(response, 401, new IllegalArgumentException("Invalid or missing authentication token."));
            return;
        }
        JsonObject requestBody = CommunicationCommons.getRequestBody(request);

        Packet packet = AdvanciusBungee.GSON.fromJson(requestBody, Packet.class);
        packet.setResponseMetadata(new Metadata());

        try {
            AdvanciusBungee.getInstance().getCommunicationManager().handlePacket(identifier, packet);

            JsonObject responseData = AdvanciusBungee.GSON.toJsonTree(packet.getResponseMetadata().getInternal()).getAsJsonObject();
            sendData(response, 200, responseData, false);
        } catch (UnhandledPacketException exception) {
            AdvanciusLogger.log(Level.WARNING, "Client [%s] sent unknown or unhandled packet servlet: %s", identifier, packet.getType());
            sendError(response, 501, exception);
        } catch (PacketListenerException exception) {
            AdvanciusLogger.log(Level.SEVERE, "Encountered error processing %s packet from %s", packet.getType(), identifier);
            sendError(response, 500, exception);
        }
    }

    private void sendError(HttpServletResponse response, int code, Exception exception) throws IOException {
        sendData(response, code, AdvanciusBungee.GSON.toJsonTree(exception).getAsJsonObject(), true);
    }

    private void sendData(HttpServletResponse response, int code, JsonObject data, boolean exception) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject responseData = new JsonObject();
        responseData.addProperty("exception", exception);
        responseData.add("data", data);

        response.getWriter().write(responseData.toString());
        response.setStatus(code);

        response.getWriter().flush();
        response.getWriter().close();
    }
}