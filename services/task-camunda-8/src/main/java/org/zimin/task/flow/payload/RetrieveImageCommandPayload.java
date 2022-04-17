package org.zimin.task.flow.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveImageCommandPayload {

    private String refId;

    private Long imageId;
    protected String email;
    protected String fact;

}
