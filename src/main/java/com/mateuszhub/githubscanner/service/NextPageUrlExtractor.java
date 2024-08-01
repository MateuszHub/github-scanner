package com.mateuszhub.githubscanner.service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NextPageUrlExtractor {
    private static final String NEXT_PAGE_PATTERN = "<([\\S]+)>; rel=\"next\"";

    public String getNextPageUrl(String linkHeader) {
        if (linkHeader == null || linkHeader.isEmpty()) {
            return null;
        }

        Pattern pattern = Pattern.compile(NEXT_PAGE_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(linkHeader);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
