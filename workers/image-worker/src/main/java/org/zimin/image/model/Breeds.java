package org.zimin.image.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Breeds {

    private Weight weight;
    private String id; //"hbro",
    private String name; //"Havana Brown",
    private String temperament; //"Affectionate, Curious, Demanding, Friendly, Intelligent, Playful",
    private String origin; //United Kingdom
    private String description;
    private String alt_names; //"Havana, HB",
    private String wikipedia_url; //"https://en.wikipedia.org/wiki/Havana_Brown",

}

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class Weight {
    private String imperial; //"6 - 10",
    private String metric; // "3 - 5"}
}
