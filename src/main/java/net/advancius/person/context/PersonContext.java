package net.advancius.person.context;

import com.google.gson.JsonObject;
import lombok.Data;
import net.advancius.person.Person;

@Data
public abstract class PersonContext {

    protected Person person;
    protected int priority;

    public abstract JsonObject serializeJson();

    public abstract void onPersonLoad() throws Exception;
    public abstract void onPersonSave() throws Exception;

    public abstract String getName();
}
