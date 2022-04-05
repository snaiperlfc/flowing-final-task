package org.zimin.image.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zimin.image.model.Fact;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveImageCommandPayload {

    private String refId;
    private String image;
    protected String email;
    protected String fact;

}
