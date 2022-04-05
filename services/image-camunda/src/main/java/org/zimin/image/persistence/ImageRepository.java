package org.zimin.image.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.zimin.image.model.Image;

@Component
public interface ImageRepository extends CrudRepository<Image, Long> {

}
