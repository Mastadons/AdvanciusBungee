package net.advancius.command;

import net.advancius.AdvanciusBungee;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.md_5.bungee.api.ProxyServer;

import java.util.ArrayList;
import java.util.List;

@FlagManager.FlaggedClass
public class CommandManager {

    private static final Class<?>[] ARGUMENT_TYPES = new Class<?>[]{String[].class, String.class, CommandFlags.class};

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 10)
    private static void commandManager() {
        CommandManager instance = new CommandManager();
        AdvanciusBungee.getInstance().setCommandManager(instance);

        for (CommandDescription commandDescription : CommandConfiguration.getInstance().getCommands())
            instance.addDescription(commandDescription);

        FlagManager.runFlaggedMethods("net.advancius", DefinedFlag.POST_COMMANDS_LOAD);
    }

    private final List<CommandDescription> descriptionList = new ArrayList<>();
    private final List<CommandListener> listenerList = new ArrayList<>();
    private final List<InternalCommand> commandList = new ArrayList<>();

    public void addDescription(CommandDescription description) {
        if (hasDescription(description.getName())) return;
        descriptionList.add(description);
    }

    public CommandDescription getDescription(String name) {
        return descriptionList.stream().filter(description -> description.hasName(name)).findFirst().orElse(null);
    }

    public boolean hasDescription(String name) {
        return getDescription(name) != null;
    }

    public void removeDescription(String name) {
        descriptionList.remove(getDescription(name));
    }

    public void registerListener(CommandListener listener) {
        if (listenerList.contains(listener)) return;
        registerCommands(listener);
        listenerList.add(listener);
    }

    public void unregisterListener(CommandListener listener) {
        if (!listenerList.contains(listener)) return;
        unregisterCommands(listener);
        listenerList.remove(listener);
    }

    private void registerCommands(CommandListener listener) {
        for (CommandHandlerMethod handlerMethod : listener.getHandlerMethods()) {
            CommandDescription description = getDescription(handlerMethod.getHandler().description());
            if (description == null) continue;

            InternalCommand command = new InternalCommand(description, listener, handlerMethod);
            ProxyServer.getInstance().getPluginManager().registerCommand(AdvanciusBungee.getInstance(), command);
            commandList.add(command);
        }
    }

    private void unregisterCommands(CommandListener listener) {
        for (InternalCommand command : commandList) {
            if (!command.getListener().equals(listener)) continue;

            ProxyServer.getInstance().getPluginManager().unregisterCommand(command);
        }
        commandList.removeIf(command -> command.getListener().equals(listener));
    }

    public void registerExternalCommand(ExternalCommand command) {
        ProxyServer.getInstance().getPluginManager().registerCommand(AdvanciusBungee.getInstance(), command);
    }

    public InternalCommand getInternalCommand(String name) {
        for (InternalCommand command : commandList)
            if (command.getDescription().hasName(name)) return command;
        return null;
    }

    public List<InternalCommand> getSubcommands(String name) {
        List<InternalCommand> subcommandList = new ArrayList<>();
        for (InternalCommand command : commandList) {
            if (command.getDescription().isSubcommandOf(name)) subcommandList.add(command);
        }
        return subcommandList;
    }

    public boolean isValidArgumentType(Class<?> argumentType) {
        for (Class<?> type : ARGUMENT_TYPES) if (type.equals(argumentType)) return true;
        return false;
    }
}
