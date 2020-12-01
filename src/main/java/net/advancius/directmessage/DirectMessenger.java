package net.advancius.directmessage;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.person.Person;
import net.advancius.person.context.BungeecordContext;
import net.advancius.person.context.MetadataContext;
import net.advancius.person.context.PermissionContext;
import net.advancius.placeholder.PlaceholderComponent;
import net.luckperms.api.cacheddata.CachedMetaData;

public class DirectMessenger {

    public static DirectMessage createDirectMessage(Person sender, Person reader, String message) {
        return new DirectMessage(sender, reader, message);
    }

    public static void sendDirectMessage(DirectMessage directMessage) {
        handleDirectMessageSender(directMessage);
        handleDirectMessageReader(directMessage);

        handleSocialSpy(directMessage);
    }

    private static void handleDirectMessageSender(DirectMessage directMessage) {
        PlaceholderComponent pc = new PlaceholderComponent(AdvanciusLang.getInstance().directMessageSender);
        replacePlaceholders(pc, directMessage);
        pc.translateColor();

        BungeecordContext.sendMessage(directMessage.getSender(), pc.toTextComponentUnsafe());

        MetadataContext metadataContext = directMessage.getSender().getContextManager().getContext("metadata");
        metadataContext.getTransientMetadata().setMetadata("LastDirectReader", directMessage.getReader().getId());
    }

    private static void handleDirectMessageReader(DirectMessage directMessage) {
        PlaceholderComponent pc = new PlaceholderComponent(AdvanciusLang.getInstance().directMessageReader);
        replacePlaceholders(pc, directMessage);
        pc.translateColor();

        BungeecordContext.sendMessage(directMessage.getReader(), pc.toTextComponentUnsafe());

        MetadataContext metadataContext = directMessage.getReader().getContextManager().getContext("metadata");
        metadataContext.getTransientMetadata().setMetadata("LastDirectSender", directMessage.getSender().getId());
    }

    private static void handleSocialSpy(DirectMessage directMessage) {
        for (Person onlinePerson : AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons()) {
            PermissionContext permissionContext = onlinePerson.getContextManager().getContext(PermissionContext.class);
            MetadataContext metadataContext = onlinePerson.getContextManager().getContext(MetadataContext.class);

            if (isExempt(directMessage.getSender()) || isExempt(directMessage.getReader())) continue;
            if (onlinePerson.equals(directMessage.getSender()) || onlinePerson.equals(directMessage.getReader())) continue;
            if (!permissionContext.hasPermission(AdvanciusBungee.getInstance().getCommandManager().getDescription("socialspy").getPermission())) continue;
            if (!metadataContext.isSocialSpy()) continue;

            PlaceholderComponent pc = new PlaceholderComponent(AdvanciusLang.getInstance().socialSpy);
            replacePlaceholders(pc, directMessage);
            pc.translateColor();

            BungeecordContext.sendMessage(onlinePerson, pc.toTextComponentUnsafe());
        }
    }

    private static void replacePlaceholders(PlaceholderComponent placeholderComponent, DirectMessage directMessage) {
        placeholderComponent.replace("message", directMessage.getMessage());
        placeholderComponent.replace("sender", directMessage.getSender());
        placeholderComponent.replace("reader", directMessage.getReader());
    }

    private static boolean isExempt(Person person) {
        PermissionContext permissionContext = person.getContextManager().getContext(PermissionContext.class);

        CachedMetaData metadata = permissionContext.getLuckpermsUser().getCachedData().getMetaData();
        String exemptStatus = metadata.getMetaValue("socialspy-exempt");
        return exemptStatus != null && exemptStatus.equalsIgnoreCase("true");
    }
}