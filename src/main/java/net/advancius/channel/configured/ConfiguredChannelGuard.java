package net.advancius.channel.configured;

import lombok.Data;
import net.advancius.channel.ChannelGuard;
import net.advancius.person.Person;
import net.advancius.person.context.PermissionContext;

@Data
public class ConfiguredChannelGuard implements ChannelGuard {

    private String readPermission;
    private String sendPermission;

    @Override
    public boolean canPersonRead(Person person) {
        PermissionContext permissions = person.getContextManager().getContext("permissions");
        return permissions.hasPermission(readPermission);
    }

    @Override
    public boolean canPersonSend(Person person) {
        PermissionContext permissions = person.getContextManager().getContext("permissions");
        return permissions.hasPermission(sendPermission);
    }
}
