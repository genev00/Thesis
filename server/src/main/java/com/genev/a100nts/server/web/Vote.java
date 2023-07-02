package com.genev.a100nts.server.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class Vote {

    @NotNull
    private Long siteId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer vote;

    @NotNull
    @Pattern(regexp = "^bg|en$")
    private String language;

}