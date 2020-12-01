package net.advancius.event;

import lombok.Data;

import java.util.UUID;

@Data
public abstract class AbstractEvent implements Event {

    protected final UUID eventId = UUID.randomUUID();
    protected final long timestamp = System.currentTimeMillis();

    protected boolean cancelled;
    protected boolean completed;
}
