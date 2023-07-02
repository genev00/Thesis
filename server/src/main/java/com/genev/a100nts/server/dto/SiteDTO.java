package com.genev.a100nts.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteDTO {

    private Long id;
    private String title;
    private String province;
    private String town;
    private String details;
    private List<String> imageUrls;
    private List<CommentDTO> comments;
    private Integer rating;
    private double latitude;
    private double longitude;

}
