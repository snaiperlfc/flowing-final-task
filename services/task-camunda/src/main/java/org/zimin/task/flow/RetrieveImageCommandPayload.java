package org.zimin.task.flow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zimin.task.domain.Fact;
import org.zimin.task.domain.Task;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveImageCommandPayload {

    private String refId;
    private Long imageId;
    protected String email;
    protected String fact;

    public RetrieveImageCommandPayload(String id, String email, String fact) {
        this.refId = id;
        this.email = email;
        this.fact = fact;
    }
}
