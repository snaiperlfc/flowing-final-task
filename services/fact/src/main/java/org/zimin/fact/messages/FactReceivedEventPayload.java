package org.zimin.fact.messages;

import lombok.Data;

@Data
public class FactReceivedEventPayload {

    private String refId;
    private String fact;

    public FactReceivedEventPayload setRefId(String refId) {
        this.refId = refId;
        return this;
    }

    public FactReceivedEventPayload setFact(String fact) {
        this.fact = fact;
        return this;
    }
}
