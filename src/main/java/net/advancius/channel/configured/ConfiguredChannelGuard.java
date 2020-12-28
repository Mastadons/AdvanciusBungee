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
        return PermissionContext.hasPermission(person, readPermission);
    }

    @Override
    public boolean canPersonSend(Person person) {
        return PermissionContext.hasPermission(person, sendPermission);
    }
}
