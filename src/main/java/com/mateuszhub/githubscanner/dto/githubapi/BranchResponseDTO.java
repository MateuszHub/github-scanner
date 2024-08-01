package com.mateuszhub.githubscanner.dto.githubapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BranchResponseDTO(String name, CommitResponseDTO commit) {
}

