package net.advancius.command;

import lombok.Data;
import net.advancius.person.Person;
import net.advancius.person.context.ConnectionContext;
import net.advancius.utils.ColorUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
public class CommandHandlerMethod {

    private final CommandHandler handler;
    private final Method method;
    private final Class<?> argumentType;

    public void invokeCommand(CommandListener listener, Person person, CommandDescription description, Object argument) {
        try {
            method.invoke(listener, person, description, argument);
        } catch (Throwable exception) {
            if (exception instanceof InvocationTargetException) exception = exception.getCause();

            ConnectionContext.sendMessage(person, ColorUtils.toTextComponent("&cEncountered " + exception.getClass().getSimpleName() + ". " + exception.getMessage()));
            if (CommandConfiguration.getInstance().isDebugExceptions()) exception.printStackTrace();
        }
    }
}
