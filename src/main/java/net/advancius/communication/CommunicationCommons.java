package net.advancius.communication;

import com.google.gson.JsonObject;
import net.advancius.AdvanciusBungee;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CommunicationCommons {

    public static JsonObject getRequestBody(HttpServletRequest request) throws IOException {
        return AdvanciusBungee.GSON.fromJson(request.getReader(), JsonObject.class);
    }
}
