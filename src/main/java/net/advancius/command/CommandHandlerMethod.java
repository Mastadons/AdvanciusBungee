package net.advancius.command;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class CommandHandlerMethod {

    private final CommandHandler handler;
    private final Method method;
    private final Class<?> argumentType;

}
