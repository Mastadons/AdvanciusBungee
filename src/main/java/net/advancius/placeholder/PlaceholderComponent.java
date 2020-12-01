package net.advancius.placeholder;

import lombok.Data;
import net.advancius.AdvanciusBungee;
import net.advancius.utils.ColorUtils;
import net.advancius.utils.Reflection;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.Point;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
public class PlaceholderComponent {

    public static final String DEFAULT_PLACEHOLDER_TEXT = "unknown";

    public static final char OPENING_CHARACTER = '{';
    public static final char CLOSING_CHARACTER = '}';

    private String text;

    private boolean replaceJson = false;

    public PlaceholderComponent(String text) { this.text = text; }

    public void replace(String original, Object replacement) {
        if (original.length() == 0 || replacement == null) return;

        List<Point> placeholderSubstrings = getPlaceholderSubstrings();
        List<String> replacementTextList = new ArrayList<>();
        for (Point substring : placeholderSubstrings) {
            String substringText = text.substring(substring.x+1, substring.y);

            String[] substringComponents = substringText.split("\\.");

            if (!substringComponents[0].equals(original)) {
                replacementTextList.add('{' + substringText + '}');
                continue;
            }

            if (substringComponents.length == 1) {
                replacementTextList.add(replacement.toString());
                continue;
            }
            Object internalReplacement = replacement;

            for (int i=1; i<substringComponents.length; ++i) {
                Method wildcardMethod = getWildcardMethod(internalReplacement);
                Method method = getPlaceholderMethod(internalReplacement, substringComponents[i]);
                Field field = getPlaceholderField(internalReplacement, substringComponents[i]);

                if (field != null)
                    internalReplacement = Reflection.accessField(internalReplacement, field);
                else if (method != null)
                    internalReplacement = Reflection.runMethod(method, internalReplacement);
                else if (wildcardMethod != null)
                    internalReplacement = Reflection.runMethod(wildcardMethod, internalReplacement, substringComponents[i]);
                else internalReplacement = null;

                if (internalReplacement == null) break;
            }

            replacementTextList.add(internalReplacement == null ? DEFAULT_PLACEHOLDER_TEXT : getReplacement(internalReplacement));
        }
        String result = text + " ";
        int increase = 0;

        for (int i=0; i<placeholderSubstrings.size(); i++) {
            Point substringRange = placeholderSubstrings.get(i);

            result = result.substring(0, substringRange.x + increase) + replacementTextList.get(i) + result.substring(substringRange.y+1 + increase);
            increase += replacementTextList.get(i).length() - (substringRange.y - substringRange.x + 1);
        }
        text = result.substring(0, result.length()-1);
    }

    private String getReplacement(Object internalReplacement) {
        if (replaceJson) return AdvanciusBungee.GSON.toJson(internalReplacement);
        return internalReplacement.toString();
    }


    private Field getPlaceholderField(Object object, String placeholder) {
        for (Field field : object.getClass().getDeclaredFields()) {
            /*if (!field.isAnnotationPresent(Placeholder.class)) continue;
            String methodPlaceholder = field.getAnnotation(Placeholder.class).value();

            if (!placeholder.equals(methodPlaceholder)) continue;
            return field;

             */
            if (field.getName().equals(placeholder)) return field;
        }
        return null;
    }

    private Method getPlaceholderMethod(Object object, String placeholder) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            /*
            if (!method.isAnnotationPresent(Placeholder.class)) continue;
            String methodPlaceholder = method.getAnnotation(Placeholder.class).value();

            if (!placeholder.equals(methodPlaceholder)) continue;
            return method;

             */
            if (method.getName().equals(placeholder)) return method;
        }
        return null;
    }

    private Method getWildcardMethod(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(WildcardPlaceholder.class)) return method;
        }
        return null;
    }

    private List<Point> getPlaceholderSubstrings() {
        List<Point> substrings = new ArrayList<>();
        int opening = 0;
        boolean inside = false;

        char[] characters = text.toCharArray();
        for (int i=0; i<characters.length; ++i) {
            if (characters[i] == OPENING_CHARACTER) {
                if (inside) continue;
                opening = i;
                inside = true;
            }
            if (characters[i] == CLOSING_CHARACTER && inside) {
                if (!inside) continue;
                inside = false;
                if (i-opening > 1) substrings.add(new Point(opening, i));
            }
        }
        return substrings;
    }

    public void translateColor() {
        text = ColorUtils.translateColor(text);
    }

    @Deprecated
    public TextComponent toTextComponentUnsafe() {
        return new TextComponent(TextComponent.fromLegacyText(text));
    }

    public BaseComponent[] toTextComponent() {
        return TextComponent.fromLegacyText(text);
    }
}
