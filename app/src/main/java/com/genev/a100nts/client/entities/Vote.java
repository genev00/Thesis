package com.genev.a100nts.client.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    private Long siteId;
    private Integer vote;
    private String language;

}
