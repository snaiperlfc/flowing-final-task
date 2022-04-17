package org.zimin.task.flow;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TaskFlowContext {

    private String traceId;

    private String email;
    private String taskId;
    private Long imageId;
    private String shipmentId;

    public static TaskFlowContext fromMap(Map<String, Object> values) {
        TaskFlowContext context = new TaskFlowContext();
        context.traceId = (String) values.get("traceId");
        context.email = (String) values.get("email");
        context.taskId = (String) values.get("taskId");
        context.imageId = (Long) values.get("imageId");
        context.shipmentId = (String) values.get("shipmentId");
        return context;
    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("traceId", traceId);
        map.put("email", email);
        map.put("taskId", taskId);
        map.put("imageId", imageId);
        map.put("shipmentId", shipmentId);
        return map;
    }

}
