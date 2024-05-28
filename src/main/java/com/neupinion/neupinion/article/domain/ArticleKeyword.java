package com.neupinion.neupinion.article.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article_keyword")
@Entity
public class ArticleKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    public ArticleKeyword(final Long id, final String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    public static ArticleKeyword forSave(final String keyword) {
        return new ArticleKeyword(null, keyword);
    }
}
