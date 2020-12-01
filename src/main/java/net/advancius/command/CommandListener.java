package net.advancius.command;

import net.advancius.AdvanciusBungee;
import net.advancius.person.Person;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public interface CommandListener {

    default Set<CommandHandlerMethod> getHandlerMethods() {
        CommandManager commandManager = AdvanciusBungee.getInstance().getCommandManager();

        Set<CommandHandlerMethod> handlerMethods = new HashSet<>();
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(CommandHandler.class)) continue;
            if (method.getParameterCount() != 3) continue;
            if (!Person.class.equals(method.getParameterTypes()[0])) continue;
            if (!CommandDescription.class.equals(method.getParameterTypes()[1])) continue;
            if (!commandManager.isValidArgumentType(method.getParameterTypes()[2])) continue;

            CommandHandler commandHandler = method.getAnnotation(CommandHandler.class);
            CommandHandlerMethod handlerMethod = new CommandHandlerMethod(commandHandler, method, method.getParameterTypes()[2]);

            handlerMethods.add(handlerMethod);
        }
        return handlerMethods;
    }
}
