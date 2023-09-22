package com.project.boardproject.dto;
// Serializable?

public record ArticleUpdateDto(
        String title,
        String content,
        String hashtag
) { // Serializable?
    public static ArticleUpdateDto of (String title, String content, String hashtag) {
        return new ArticleUpdateDto(title, content, hashtag);
    }
}
