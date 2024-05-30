package com.neupinion.neupinion.article.domain.repository;

import com.neupinion.neupinion.article.domain.Article;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findAllByArticleKeywordId(final Long articleKeywordId);
}
