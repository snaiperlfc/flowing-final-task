package org.zimin.image.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Image {
    @Id
    @JsonProperty("imageId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected byte[] data;

    public Image(byte[] data) {
        this.data = data;
    }
}
