package com.mateuszhub.githubscanner.dto;

import java.util.List;

public record RepoDTO(String repositoryName, String userLogin, List<BranchDTO> branches) { }
