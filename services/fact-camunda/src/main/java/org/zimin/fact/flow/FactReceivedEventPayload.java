package org.zimin.fact.flow;

import lombok.Data;
import org.zimin.fact.model.Fact;

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
