package net.advancius.communication;

import net.advancius.AdvanciusLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class RequestManager {

    private final Map<UUID, AtomicReference<CommunicationPacket>> requestMap = new HashMap<>();

    public boolean handleRequest(CommunicationPacket communicationPacket) {
        if (communicationPacket.getRespondingTo() == null) return false;
        requestMap.forEach((requestId, reference) -> {
            if (!communicationPacket.getRespondingTo().equals(requestId)) return;
            AdvanciusLogger.log(Level.INFO, "[Network] Handling incoming response(%s) with code %d",
                    communicationPacket.getId().toString(), communicationPacket.getCode());
            reference.set(communicationPacket);
        });
        return requestMap.remove(communicationPacket.getRespondingTo()) != null;
    }

    public void awaitResponse(CommunicationPacket communicationPacket, AtomicReference<CommunicationPacket> reference) {
        requestMap.put(communicationPacket.getId(), reference);
    }
}
