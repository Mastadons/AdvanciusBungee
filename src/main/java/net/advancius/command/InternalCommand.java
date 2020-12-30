package net.advancius.command;

import lombok.Getter;
import net.advancius.AdvanciusBungee;
import net.advancius.person.Person;
import net.advancius.utils.ColorUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class InternalCommand extends Command {

    @Getter private final CommandDescription description;
    @Getter private final CommandListener listener;
    @Getter private final CommandHandlerMethod handlerMethod;

    public InternalCommand(CommandDescription description, CommandListener listener, CommandHandlerMethod handlerMethod) {
        super(description.getName(), description.getPermission(), description.getAliases().toArray(new String[0]));
        this.description = description;
        this.listener = listener;
        this.handlerMethod = handlerMethod;
    }

    @Override
    public void execute(CommandSender commandSender, String[] arguments) {
        Person person = null;
        if (commandSender instanceof ProxiedPlayer) {
            person = AdvanciusBungee.getInstance().getPersonManager().getPerson(((ProxiedPlayer) commandSender).getUniqueId());
        }

        if (invokeSubcommand(commandSender, arguments)) return;

        if (isUnhandledInterface(person)) {
            commandSender.sendMessage(ColorUtils.toTextComponent("&cThis interface is not supported by this command."));
            return;
        }

        handlerMethod.invokeCommand(listener, person, description, getArgument(arguments));
    }

    private boolean invokeSubcommand(CommandSender commandSender, String[] arguments) {
        InternalCommand subcommand = null;
        String subcommandName = description.getName();
        int argumentIndex = 0;
        for (;argumentIndex<arguments.length; argumentIndex++) {
            subcommandName += "." + arguments[argumentIndex];
            InternalCommand command = AdvanciusBungee.getInstance().getCommandManager().getInternalCommand(subcommandName);
            if (command == null) break;
            subcommand = command;
        }
        if (subcommand != null) {
            subcommand.execute(commandSender, Arrays.copyOfRange(arguments, argumentIndex, arguments.length));
        }
        return subcommand != null;
    }

    private boolean isUnhandledInterface(Person person) {
        return person == null || (person != null && !handlerMethod.getMethod().getParameterTypes()[0].isAssignableFrom(person.getClass()));
    }

    private Object getArgument(String[] arguments) {
        Class<?> argumentType = handlerMethod.getMethod().getParameterTypes()[2];
        if (argumentType.equals(String[].class))
            return arguments;
        if (argumentType.equals(String.class))
            return String.join(" ", arguments);
        if (argumentType.equals(CommandFlags.class))
            return CommandFlagParser.getCommandFlags(String.join(" ", arguments));
        throw new RuntimeException();
    }
}
