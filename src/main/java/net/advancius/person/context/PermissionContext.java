package net.advancius.person.context;

import lombok.Data;
import net.advancius.person.Person;
import org.jetbrains.annotations.NotNull;

@Data
public abstract class PermissionContext extends PersonContext {

    public static boolean hasPermission(@NotNull Person person, String permission) {
        return person.getContextManager().getContext(PermissionContext.class).hasPermission(permission);
    }

    public abstract boolean hasPermission(String permission);

    public abstract String getPrefix();
    public abstract String getSuffix();

    public abstract boolean isSocialSpyExempt();
    public abstract boolean isCommandSpyExempt();
    public abstract boolean isIgnoreExempt();

    @Override
    public final String getName() {
        return "permissions";
    }
}
