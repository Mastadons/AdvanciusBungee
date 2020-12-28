package net.advancius.person;

import com.google.gson.JsonParser;
import net.advancius.AdvanciusBungee;
import net.advancius.file.FileManager;
import net.advancius.flag.DefinedFlag;
import net.advancius.flag.FlagManager;
import net.advancius.person.context.ConnectionContext;
import net.advancius.player.PlayerPerson;
import net.advancius.person.event.PersonLoadEvent;
import net.advancius.person.event.PersonSaveEvent;
import net.advancius.event.Event;
import net.advancius.utils.Metadata;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@FlagManager.FlaggedClass
public class PersonManager {

    @FlagManager.FlaggedMethod(flag = DefinedFlag.PLUGIN_LOAD, priority = 20)
    private static void personManager() {
        AdvanciusBungee.getInstance().setPersonManager(new PersonManager());
    }

    private final List<Person> personList = new ArrayList<>();

    public Person getPerson(UUID id) {
        return personList.stream().filter(person -> person.getId().equals(id)).findFirst().orElse(null);
    }

    public Person getPersonUnsafe(Object object) {
        if (!(object instanceof ProxiedPlayer)) throw new RuntimeException();
        return getPerson(((ProxiedPlayer) object).getUniqueId());
    }

    public Person loadPerson(UUID id) {
        Person existentPerson = getPerson(id);
        if (existentPerson != null) return existentPerson;

        Person person = new PlayerPerson(id);

        Event loadEvent = AdvanciusBungee.getInstance().getEventManager().generateEvent(PersonLoadEvent.class, person);
        AdvanciusBungee.getInstance().getEventManager().executeEvent(loadEvent);

        personList.add(person);
        return person;
    }

    public void savePerson(UUID id) {
        Person person = getPerson(id);

        if (person == null) return;

        Event saveEvent = AdvanciusBungee.getInstance().getEventManager().generateEvent(PersonSaveEvent.class, person);
        AdvanciusBungee.getInstance().getEventManager().executeEvent(saveEvent);
        personList.remove(person);
    }

    public List<Person> getOnlinePersons() {
        return new ArrayList<>(personList);
    }

    public List<Person> getOnlinePersons(Predicate<Person> predicate) {
        return getOnlinePersons().stream().filter(predicate).collect(Collectors.toList());
    }

    public void broadcastMessage(TextComponent component) {
        personList.forEach(person -> ConnectionContext.sendMessage(person, component));
    }

    public Person getOnlinePerson(String name) {
        return personList.stream().filter(person -> person.getContextManager().getContext(ConnectionContext.class).getConnectionName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public UUID getOfflinePerson(String name) {
        File personDirectory = FileManager.getServerFile("persons");
        for (File personFile : personDirectory.listFiles((file, filename) -> filename.endsWith(".json"))) {
            try {
                Metadata persistentMetadata = new Metadata();
                persistentMetadata.deserialize(new JsonParser().parse(new String(Files.readAllBytes(personFile.toPath()))).getAsJsonObject());

                if (!persistentMetadata.hasMetadata("last_username")) continue;
                String username = persistentMetadata.getMetadata("last_username", String.class);
                if (username.equalsIgnoreCase(name)) return UUID.fromString(personFile.getName().split("\\.")[0]);
            } catch (Exception ignored) {}
        }
        return null;
    }
}
