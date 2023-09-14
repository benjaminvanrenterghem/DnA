package com.daimler.data.dto;

import com.daimler.data.controller.exceptions.MessageDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatomoGetSiteResponseDto {
    private String result;
    private String  message;
    private String idsite;
    private String name;
    private String main_url;
    private List<MessageDescription> errors;
    private String status;
    private List<MessageDescription> warnings;
}