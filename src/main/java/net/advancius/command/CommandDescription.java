package net.advancius.command;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.advancius.utils.Metadata;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CommandDescription {

    private String name;
    private String permission;
    private String syntax;
    private String description;
    private List<String> aliases = new ArrayList<>();

    private Metadata metadata = new Metadata();

    public CommandDescription(String name, String permission, String description, List<String> aliases) {
        this.name = name;
        this.permission = permission;
        this.description = description;
        this.aliases = aliases;
    }

    public boolean hasName(String argument) {
        if (name.equalsIgnoreCase(argument)) return true;
        for (String alias : aliases) if (alias.equalsIgnoreCase(argument)) return true;
        return false;
    }

    public boolean isSubcommand() {
        return name.contains(".");
    }

    public boolean isSubcommandOf(String name) {
        String[] nameComponents =      name.split("\\.");
        String[] selfComponents = this.name.split("\\.");

        if (nameComponents.length >= selfComponents.length) return false;
        for (int i=0; i<nameComponents.length; i++)
            if (!nameComponents[i].equalsIgnoreCase(selfComponents[i])) return false;
        return true;
    }
}
