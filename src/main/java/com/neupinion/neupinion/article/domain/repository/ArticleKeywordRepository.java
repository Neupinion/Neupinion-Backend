package com.neupinion.neupinion.article.domain.repository;

import com.neupinion.neupinion.article.domain.ArticleKeyword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleKeywordRepository extends JpaRepository<ArticleKeyword, Long> {

    Optional<ArticleKeyword> findByKeyword(String keyword);
}
