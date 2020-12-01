package net.advancius.command;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Command;

public abstract class ExternalCommand extends Command {

    @Getter protected final CommandDescription description;

    public ExternalCommand(CommandDescription description) {
        super(description.getName(), description.getPermission(), description.getAliases().toArray(new String[0]));
        this.description = description;
    }
}
