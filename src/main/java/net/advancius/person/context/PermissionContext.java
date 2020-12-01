package net.advancius.person.context;

import com.google.gson.JsonObject;
import lombok.Data;
import net.advancius.person.Person;
import net.advancius.person.context.PersonContext;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

@Data
public class PermissionContext extends PersonContext {

    private User luckpermsUser;

    public static boolean hasPermission(Person person, String permission) {
        return person.getContextManager().getContext(PermissionContext.class).hasPermission(permission);
    }

    public boolean hasPermission(String permission) {
        ContextManager contextManager = LuckPermsProvider.get().getContextManager();
        ImmutableContextSet contextSet = contextManager.getContext(luckpermsUser).orElseGet(contextManager::getStaticContext);

        CachedPermissionData permissionData = luckpermsUser.getCachedData().getPermissionData(QueryOptions.contextual(contextSet));
        return permissionData.checkPermission(permission).asBoolean();
    }

    public CachedMetaData getMetadata() {
        return luckpermsUser.getCachedData().getMetaData();
    }

    public String getPrefix() {
        CachedMetaData metadata = luckpermsUser.getCachedData().getMetaData();
        return metadata.getPrefix() != null ? metadata.getPrefix() : "";
    }

    public String getSuffix() {
        CachedMetaData metadata = luckpermsUser.getCachedData().getMetaData();
        return metadata.getSuffix() != null ? metadata.getSuffix() : "";
    }

    @Override
    public JsonObject serializeJson() {
        return new JsonObject();
    }

    @Override
    public void onPersonLoad() {
        luckpermsUser = LuckPermsProvider.get().getUserManager().getUser(person.getId());
    }

    @Override
    public void onPersonSave() {}

    @Override
    public String getName() {
        return "permissions";
    }
}
