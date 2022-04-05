package org.zimin.task.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import org.zimin.task.domain.Task;

@Component
public interface TaskRepository extends CrudRepository<Task, String> {

}
