package org.zimin.monitor.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PastEvent {

    private String transactionId;
    private String type;
    private String name;
    private String content;
    private String sender;

    private String sourceJson;

    public PastEvent(String type, String name, String transactionId, String sender, String eventContent) {
        this.transactionId = transactionId;
        this.type = type;
        this.name = name;
        this.sender = sender;
        this.content = eventContent;
    }


}
