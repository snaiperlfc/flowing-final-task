package org.zimin.task.flow.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveFactCommandPayload {

    private String refId;
    private String fact;

}
