package org.zimin.task.flow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zimin.task.domain.Fact;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveFactCommandPayload {

    private String refId;
    private String fact;

}
