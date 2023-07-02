package com.genev.a100nts.client.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Site {

    private Long id;
    private String title;
    private String titleBG;
    private String province;
    private String provinceBG;
    private String town;
    private String townBG;
    private String description;
    private String descriptionBG;
    private String latitude;
    private String longitude;
    private boolean visible;
    private List<String> imagesUI;

}
