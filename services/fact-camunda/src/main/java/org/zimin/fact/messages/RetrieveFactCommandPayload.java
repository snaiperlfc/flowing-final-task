package org.zimin.fact.messages;

import lombok.Data;
import org.zimin.fact.model.Fact;

@Data
public class RetrieveFactCommandPayload {

    private String refId;
    private String fact;

}
