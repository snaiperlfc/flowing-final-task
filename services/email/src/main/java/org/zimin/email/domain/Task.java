package org.zimin.email.domain;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Task {

  private String taskId = "email-generated-" + UUID.randomUUID();
  protected String email;

}
