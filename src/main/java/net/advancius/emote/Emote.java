package net.advancius.emote;

import net.advancius.person.Person;

public interface Emote {

    void sendEmote(Person sender, String message);
}
