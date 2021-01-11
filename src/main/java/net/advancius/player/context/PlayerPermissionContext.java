package net.advancius.player.context;

import com.google.gson.JsonObject;
import lombok.Data;
import net.advancius.person.context.PermissionContext;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

@Data
public class PlayerPermissionContext extends PermissionContext {

    private User luckpermsUser;

    @Override
    public boolean hasPermission(String permission) {
        ContextManager contextManager = LuckPermsProvider.get().getContextManager();
        ImmutableContextSet contextSet = contextManager.getContext(luckpermsUser).orElseGet(contextManager::getStaticContext);

        CachedPermissionData permissionData = luckpermsUser.getCachedData().getPermissionData(QueryOptions.contextual(contextSet));
        return permissionData.checkPermission(permission).asBoolean();
    }

    public CachedMetaData getMetadata() {
        return luckpermsUser.getCachedData().getMetaData();
    }

    @Override
    public String getPrefix() {
        CachedMetaData metadata = luckpermsUser.getCachedData().getMetaData();
        return metadata.getPrefix() != null ? metadata.getPrefix() : "";
    }

    @Override
    public String getSuffix() {
        CachedMetaData metadata = luckpermsUser.getCachedData().getMetaData();
        return metadata.getSuffix() != null ? metadata.getSuffix() : "";
    }

    @Override
    public boolean isSocialSpyExempt() {
        CachedMetaData metadata = luckpermsUser.getCachedData().getMetaData();
        String exemptStatus = metadata.getMetaValue("socialspy-exempt");
        return exemptStatus != null && exemptStatus.equalsIgnoreCase("true");
    }

    @Override
    public boolean isCommandSpyExempt() {
        CachedMetaData metadata = luckpermsUser.getCachedData().getMetaData();
        String exemptStatus = metadata.getMetaValue("commandspy-exempt");
        return exemptStatus != null && exemptStatus.equalsIgnoreCase("true");
    }

    @Override
    public boolean isIgnoreExempt() {
        CachedMetaData metadata = luckpermsUser.getCachedData().getMetaData();
        String exemptStatus = metadata.getMetaValue("ignore-exempt");
        return exemptStatus != null && exemptStatus.equalsIgnoreCase("true");
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
    public void onPersonSave() {
    }
}
