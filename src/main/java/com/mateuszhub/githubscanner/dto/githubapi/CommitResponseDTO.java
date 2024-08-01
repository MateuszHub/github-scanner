package com.mateuszhub.githubscanner.dto.githubapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommitResponseDTO(String sha) {
}
