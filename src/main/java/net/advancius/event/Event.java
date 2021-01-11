package net.advancius.event;

import java.util.UUID;

public interface Event {

    UUID getEventId();

    long getTimestamp();

    void eventCompleted();

    void eventCancelled();

    void setCancelled(boolean cancelled);

    void setCompleted(boolean completed);

    boolean isCancelled();

    boolean isCompleted();

    boolean isCancellable();
}
