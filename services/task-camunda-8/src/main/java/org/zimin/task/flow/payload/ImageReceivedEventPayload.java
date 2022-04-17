package org.zimin.task.flow.payload;

import lombok.Data;

@Data
public class ImageReceivedEventPayload {

    private String refId;
    private Long imageId;

    public ImageReceivedEventPayload setRefId(String refId) {
        this.refId = refId;
        return this;
    }

    public ImageReceivedEventPayload setImageId(Long imageId) {
        this.imageId = imageId;
        return this;
    }
}
