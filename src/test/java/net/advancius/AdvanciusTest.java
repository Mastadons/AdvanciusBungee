package net.advancius;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class AdvanciusTest {

    public static void main(String[] arguments) throws Exception {
        HttpClient connection = new HttpClient(new SslContextFactory(true));
        connection.start();

        Request request = connection.POST("https://discord.com/api/webhooks/798068808068759563/GZTboh3L4vK5K_ozkJA2mCJL1zIRkHrW-i1X45sHgTqc0EWad_-MMiYIeLt9YGcP7HKc");

        request.header(HttpHeader.CONTENT_TYPE, "application/json");

        request.content(new StringContentProvider("{\"content\":\"test from intellij\"}"), "application/json");

        ContentResponse response = request.send();

        connection.stop();
    }
}
