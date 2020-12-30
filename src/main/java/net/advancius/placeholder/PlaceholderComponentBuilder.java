package net.advancius.placeholder;

import lombok.Data;
import net.advancius.person.Person;
import net.advancius.person.context.ConnectionContext;
import net.md_5.bungee.api.chat.TextComponent;

@Data
public class PlaceholderComponentBuilder {

    private final PlaceholderComponent placeholderComponent;

    public static PlaceholderComponentBuilder create(String text) {
        return new PlaceholderComponentBuilder(text);
    }

    public static PlaceholderComponentBuilder create(PlaceholderComponent placeholderComponent) {
        return new PlaceholderComponentBuilder(placeholderComponent);
    }

    public PlaceholderComponentBuilder(String text) { this.placeholderComponent = new PlaceholderComponent(text); }

    public PlaceholderComponentBuilder replace(String original, Object replacement) {
        placeholderComponent.replace(original, replacement);
        return this;
    }

    public PlaceholderComponentBuilder translateColor() {
        placeholderComponent.translateColor();
        return this;
    }

    public TextComponent toTextComponent() {
        return placeholderComponent.toTextComponent();
    }

    public void sendColored(Person person) {
        translateColor().send(person);
    }

    public void send(Person person) {
        ConnectionContext.sendMessage(person, toTextComponent());
    }
}
