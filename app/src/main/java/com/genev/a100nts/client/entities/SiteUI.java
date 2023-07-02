package com.genev.a100nts.client.entities;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteUI implements Serializable {

    private Long id;
    private String title;
    private String province;
    private String town;
    private String details;
    private Integer rating;
    private double latitude;
    private double longitude;
    private List<String> imageUrls;
    private List<Comment> comments;

}
