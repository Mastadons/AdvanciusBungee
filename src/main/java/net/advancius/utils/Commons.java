package net.advancius.utils;

import net.advancius.AdvanciusLang;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;
import net.advancius.placeholder.PlaceholderComponent;

public class Commons {

    public static boolean onCooldown(Person person, long previous, long cooldown, boolean message) {
        long next = previous + cooldown;
        if (next < System.currentTimeMillis()) return false;

        if (message) {
            PlaceholderComponent pc = new PlaceholderComponent(AdvanciusLang.getInstance().commandCooldown);
            pc.replace("cooldown", Math.round((next - System.currentTimeMillis()) / 1000.0));
            pc.translateColor();
            BungeecordContext.sendMessage(person, pc.toTextComponentUnsafe());
        }
        return true;
    }
}
