package net.advancius;

import lombok.Data;
import net.advancius.placeholder.WildcardPlaceholder;
import net.advancius.placeholder.PlaceholderComponent;

import java.io.IOException;

public class AdvanciusTest {

    public static void main(String[] arguments) throws IOException {
        String message = "Hello Clans_MC";
        String username = "Clans_MC";

        System.out.println(message.toLowerCase().contains(username.toLowerCase()));
    }
}
