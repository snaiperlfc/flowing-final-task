package org.zimin.image.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatApiResponse {

    private String id;// "eJUcA5Wt-"
    private String url; //"https://cdn2.thecatapi.com/images/eJUcA5Wt-.jpg",
    private Integer width; // 1250,
    private Integer height; //2014

    private List<Breeds> breeds;
}
