package com.mateuszhub.githubscanner.dto.githubapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RepoResponseDTO(String url, Boolean fork, String name, OwnerResponseDTO owner) {
}
