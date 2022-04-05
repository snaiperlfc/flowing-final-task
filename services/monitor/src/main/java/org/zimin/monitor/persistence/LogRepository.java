package org.zimin.monitor.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zimin.monitor.domain.PastEvent;

public class LogRepository {

    public static LogRepository instance = new LogRepository();

    private Map<String, List<PastEvent>> events = new HashMap<>();

    public Map<String, List<PastEvent>> getAllPastEvents() {
        return events;
    }

    public List<PastEvent> getAllPastEvents(String transactionId) {
        return events.get(transactionId);
    }

    public void addEvent(PastEvent pastEvent) {
        if (!events.containsKey(pastEvent.getTransactionId())) {
            events.put(pastEvent.getTransactionId(), new ArrayList<>());
        }
        events.get(pastEvent.getTransactionId()).add(pastEvent);
    }

}
