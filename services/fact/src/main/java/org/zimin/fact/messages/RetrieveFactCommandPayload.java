package org.zimin.fact.messages;

import lombok.Data;

@Data
public class RetrieveFactCommandPayload {

    private String refId;
    private String fact;
}
