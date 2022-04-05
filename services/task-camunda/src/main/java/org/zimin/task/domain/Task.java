package org.zimin.task.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
public class Task {

    @Id
    @JsonProperty("taskId")
    protected String id;
    protected String email;

}
