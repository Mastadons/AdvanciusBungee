package net.advancius.directmessage;

import net.advancius.AdvanciusBungee;
import net.advancius.AdvanciusLang;
import net.advancius.person.Person;
import net.advancius.person.context.MetadataContext;
import net.advancius.person.context.PermissionContext;
import net.advancius.placeholder.PlaceholderComponent;

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
        PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().directMessageSender);
        replacePlaceholders(component, directMessage);
        component.translateColor();
        component.send(directMessage.getSender());

        MetadataContext.getTransientMetadata(directMessage.getSender()).setMetadata("LastDirectReader", directMessage.getReader().getId());
    }

    private static void handleDirectMessageReader(DirectMessage directMessage) {
        PlaceholderComponent component = new PlaceholderComponent(AdvanciusLang.getInstance().directMessageReader);
        replacePlaceholders(component, directMessage);
        component.translateColor();
        component.send(directMessage.getReader());

        MetadataContext.getTransientMetadata(directMessage.getReader()).setMetadata("LastDirectSender", directMessage.getSender().getId());
    }

    private static void handleSocialSpy(DirectMessage directMessage) {
        for (Person onlinePerson : AdvanciusBungee.getInstance().getPersonManager().getOnlinePersons()) {
            PermissionContext permissionContext = onlinePerson.getContextManager().getContext(PermissionContext.class);
            MetadataContext metadataContext = onlinePerson.getContextManager().getContext(MetadataContext.class);

            if (isSocialSpyExempt(directMessage.getSender()) || isSocialSpyExempt(directMessage.getReader())) continue;
            if (onlinePerson.equals(directMessage.getSender()) || onlinePerson.equals(directMessage.getReader())) continue;
            if (!permissionContext.hasPermission(AdvanciusBungee.getInstance().getCommandManager().getDescription("socialspy").getPermission())) continue;
            if (!metadataContext.isSocialSpy()) continue;

            PlaceholderComponent pc = new PlaceholderComponent(AdvanciusLang.getInstance().socialSpy);
            replacePlaceholders(pc, directMessage);
            pc.translateColor();
            pc.send(onlinePerson);
        }
    }

    private static void replacePlaceholders(PlaceholderComponent placeholderComponent, DirectMessage directMessage) {
        placeholderComponent.replace("message", directMessage.getMessage());
        placeholderComponent.replace("sender", directMessage.getSender());
        placeholderComponent.replace("reader", directMessage.getReader());
    }

    private static boolean isSocialSpyExempt(Person person) {
        return person.getContextManager().getContext(PermissionContext.class).isSocialSpyExempt();
    }
}