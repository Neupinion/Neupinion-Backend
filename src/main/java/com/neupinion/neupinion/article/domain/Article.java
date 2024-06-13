package com.neupinion.neupinion.article.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article")
@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "original_link", nullable = false)
    private String originalLink;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "pub_date", nullable = false)
    private LocalDateTime pubDate;

    @Column(name = "stand", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Stand stand;

    @Column(name = "selected_stand", nullable = false)
    private String selectedStand;

    @Column(name = "article_keyword_id", nullable = false)
    private Long articleKeywordId;

    @Column(name = "reason", length = 10000)
    private String reason;

    public Article(final Long id, final String title, final String originalLink, final String description,
                   final LocalDateTime pubDate, final Stand stand, final String selectedStand,
                   final Long articleKeywordId, final String reason) {
        this.id = id;
        this.title = title;
        this.originalLink = originalLink;
        this.description = description;
        this.pubDate = pubDate;
        this.stand = stand;
        this.selectedStand = selectedStand;
        this.articleKeywordId = articleKeywordId;
        this.reason = reason;
    }

    public static Article forSave(final String title, final String originalLink, final String description,
                                  final LocalDateTime pubDate, final Stand stand, final String selectedStand,
                                  final Long articleKeywordId, final String reason) {
        return new Article(null, title, originalLink, description, pubDate, stand, selectedStand, articleKeywordId,
                           reason);
    }

    public boolean isSameArticle(final String title, final String originalLink) {
        return this.title.equals(title) && this.originalLink.equals(originalLink);
    }
}
