package com.neupinion.neupinion.article.application;

import com.neupinion.neupinion.article.application.dto.AnalyzedArticleResponse;
import com.neupinion.neupinion.article.application.dto.ArticleResponse;
import com.neupinion.neupinion.article.application.dto.ArticleSearchRequest;
import com.neupinion.neupinion.article.application.dto.NaverNewsItemResponse;
import com.neupinion.neupinion.article.application.dto.NaverNewsResponse;
import com.neupinion.neupinion.article.domain.Article;
import com.neupinion.neupinion.article.domain.ArticleKeyword;
import com.neupinion.neupinion.article.domain.Stand;
import com.neupinion.neupinion.article.domain.TagInfo;
import com.neupinion.neupinion.article.domain.repository.ArticleKeywordRepository;
import com.neupinion.neupinion.article.domain.repository.ArticleRepository;
import com.neupinion.neupinion.article.exception.ArticleException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ArticleService {

    private static final String CLIENT_ID_HEADER = "X-Naver-Client-Id";
    private static final String CLIENT_SECRET_HEADER = "X-Naver-Client-Secret";
    private static final String NAVER_API_URL = "https://openapi.naver.com/v1/search/news.json?query=%s&display=30";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
        "EEE, dd MMM yyyy HH:mm:ss Z",
        Locale.ENGLISH);
    private static final Map<String, TagInfo> domainTagMapping = new HashMap<>() {{
        put("www.topstarnews.net", TagInfo.ofByClass("div", "article-view-page clearfix"));
        put("www.hansbiz.co.kr", TagInfo.ofByClass("div", "article-veiw-body view-page font-size17"));
        put("www.yna.co.kr", TagInfo.ofByClass("article", "story-news article"));
        put("www.etoday.co.kr", TagInfo.ofByClass("div", "articleView"));
        put("view.asiae.co.kr", TagInfo.ofByClass("div", "cont_article"));
        put("www.newsis.com", TagInfo.ofByClass("article", ""));
        put("www.ytn.co.kr", TagInfo.ofByClass("div", "paragraph"));
        put("www.wikitree.co.kr", TagInfo.ofByClass("div", "article_body"));
        put("www.insight.co.kr", TagInfo.ofByClass("div", "news-read-content-memo"));
        put("www.hankyung.com", TagInfo.ofByClass("div", "article-body"));
        put("www.newdaily.co.kr", TagInfo.ofById("div", "article_conent"));
        put("www.segye.com", TagInfo.ofByClass("div", "viewBox2"));
        put("www.osen.co.kr", TagInfo.ofByClass("div", "view-cont"));
        put("www.nocutnews.co.kr", TagInfo.ofById("div", "article_body"));
        put("www.kookje.co.kr", TagInfo.ofByClass("div", "news_article"));
        put("www.chosun.com", TagInfo.ofByClass("div", "article-body"));
        put("news.kbs.co.kr", TagInfo.ofByClass("div", "detail-body font-size"));
        put("www.joongang.co.kr", TagInfo.ofByClass("section", "contents"));
        put("news.sbs.co.kr", TagInfo.ofByClass("div", "text_area"));
        put("news.jtbc.co.kr", TagInfo.ofByClass("div", "article_content"));
        put("www.kmib.co.kr", TagInfo.ofById("div", "articleBody"));
        put("www.edaily.co.kr", TagInfo.ofByClass("div", "news_body"));
        put("h21.hani.co.kr", TagInfo.ofByClass("div", "arti-txt"));
        put("imnews.imbc.com", TagInfo.ofByClass("div", "news_txt"));
        put("www.khan.co.kr", TagInfo.ofByClass("div", "art_body"));
        put("www.tvdaily.co.kr", TagInfo.ofByClass("div", "articles_detail"));
        put("www.sedaily.com", TagInfo.ofByClass("section", "article-body"));
        put("mydail.co.kr", TagInfo.ofByClass("div", "article_content"));
        put("news.mt.co.kr", TagInfo.ofByClass("div", "desc_body"));
        put("topstarnews.net", TagInfo.ofByClass("div", "article-view-page clearfix"));
        put("mk.co.kr", TagInfo.ofByClass("div", "view_content"));
        put("tvreport.co.kr", TagInfo.ofByClass("div", "news-view news-view-type1"));
        put("mbnmoney.mbn.co.kr", TagInfo.ofByClass("div", "detail_box"));
        put("www.news1.kr", TagInfo.ofByClass("div", "detail sa_area"));
        put("www.ohmynews.com", TagInfo.ofByClass("div", "at_contents"));
        put("www.kpinews.kr", TagInfo.ofByClass("div", "viewConts"));
        put("www.hani.co.kr", TagInfo.ofByClass("div", "article-text"));
        put("www.skyedaily.com", TagInfo.ofByClass("div", "articletext"));
        put("www.busan.com", TagInfo.ofByClass("div", "article_content"));
        put("www.newscj.com", TagInfo.ofByClass("div", "grid body"));
        put("www.imaeil.com", TagInfo.ofByClass("div", "article_content"));
        put("www.ajunews.com", TagInfo.ofByClass("div", "article_con"));
    }};
    private static final int CONTENT_THRESHOLD = 16300;
    private static final int ABSTRACT_TOKEN_SIZE = 4;

    @Value("${naver-api.client.id}")
    private String clientId;

    @Value("${naver-api.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final ChatGptService chatGptService;
    private final ArticleRepository articleRepository;
    private final ArticleKeywordRepository articleKeywordRepository;

    @Transactional
    public AnalyzedArticleResponse getArticles(final ArticleSearchRequest request) {
        final ArticleKeyword articleKeyword = articleKeywordRepository.save(
            ArticleKeyword.forSave(request.getSearchKeyword()));

        final ResponseEntity<NaverNewsResponse> response = getArticlesFromNaver(request.getSearchKeyword());
        final LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        final List<Mono<Article>> articleMonos = Objects.requireNonNull(response.getBody()).getItems().parallelStream()
            .map(item -> getArticleMono(request, item, oneMonthAgo, articleKeyword))
            .toList();

        final List<ArticleResponse> responses = Flux.merge(articleMonos)
            .collectList()
            .map(articles -> articles.stream()
                .map(article -> new ArticleResponse(article.getId(), article.getTitle(), article.getDescription(),
                                                    article.getOriginalLink(), article.getPubDate(),
                                                    article.getStand().name(), article.getReason()))
                .toList())
            .block();

        return distributeArticlesByStand(responses);
    }

    private ResponseEntity<NaverNewsResponse> getArticlesFromNaver(final String searchKeyword) {
        final String encodedKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
        final URI uri = URI.create(String.format(NAVER_API_URL, encodedKeyword));
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CLIENT_ID_HEADER, clientId);
        headers.set(CLIENT_SECRET_HEADER, clientSecret);

        final RequestEntity<Void> request = RequestEntity.get(uri)
            .headers(headers)
            .build();

        return restTemplate.exchange(request, new ParameterizedTypeReference<>() {
        });
    }

    private Mono<Article> getArticleMono(final ArticleSearchRequest request, final NaverNewsItemResponse item,
                                         final LocalDateTime oneMonthAgo, final ArticleKeyword articleKeyword) {
        final LocalDateTime publishedDate = LocalDateTime.parse(item.getPubDate(), DATE_TIME_FORMATTER);
        if (publishedDate.isBefore(oneMonthAgo)) {
            return Mono.empty();
        }
        final String articleTitle = Jsoup.parse(item.getTitle()).text();
        return getAnalyzedArticle(request, item, articleTitle, publishedDate, articleKeyword);
    }

    private Mono<Article> getAnalyzedArticle(final ArticleSearchRequest request, final NaverNewsItemResponse item,
                                             final String articleTitle, final LocalDateTime publishedDate,
                                             final ArticleKeyword articleKeyword) {
        try {
            final Document document = Jsoup.connect(item.getOriginallink()).get();
            final String domain = extractDomain(item.getOriginallink());
            final TagInfo tagInfo = domainTagMapping.get(domain);

            if (tagInfo != null) {
                final Element articleBody;
                if (tagInfo.isClass()) {
                    articleBody = document.selectFirst(tagInfo.getTag() + "." + tagInfo.getClassName());
                } else {
                    articleBody = document.selectFirst(tagInfo.getTag() + "#" + tagInfo.getIdName());
                }
                if (articleBody != null) {
                    if(countToken(articleBody) > CONTENT_THRESHOLD) {
                        return Mono.empty();
                    }
                    final String content = articleBody.text();
                    return chatGptService.getAnalyzedResult(request, content)
                        .map(analyzedResult -> Article.forSave(
                            articleTitle, item.getOriginallink(), item.getDescription(), publishedDate,
                            Stand.fromByValue(analyzedResult.getCategory()), request.getSelectedStand(),
                            articleKeyword.getId(), analyzedResult.getReason()
                        ))
                        .flatMap(article -> Mono.fromCallable(() -> articleRepository.save(article))
                            .subscribeOn(Schedulers.boundedElastic()));
                }
            }
        } catch (Exception e) {
            log.error("Failed to get an analyzed article: {}, error: {}", item.getOriginallink(), e.getMessage());
            return Mono.empty();
        }
        return Mono.empty();
    }

    private int countToken(final Element articleBody) {
        return articleBody.text().length() / ABSTRACT_TOKEN_SIZE;
    }

    private String extractDomain(final String originalLink) {
        try {
            final URI uri = new URI(originalLink);
            return uri.getHost();
        } catch (URISyntaxException e) {
            throw new ArticleException.NotFoundDomainException(Map.of("originalLink", originalLink));
        }
    }

    private AnalyzedArticleResponse distributeArticlesByStand(final List<ArticleResponse> responses) {
        final List<ArticleResponse> positiveArticles = new ArrayList<>();
        final List<ArticleResponse> neutralArticles = new ArrayList<>();
        final List<ArticleResponse> negativeArticles = new ArrayList<>();

        for (ArticleResponse article : responses) {
            final Stand stand = Stand.fromByName(article.getStand());
            if (stand == Stand.ADVANTAGEOUS) {
                positiveArticles.add(article);
            } else if (stand == Stand.NEUTRAL) {
                neutralArticles.add(article);
            } else if (stand == Stand.DISADVANTAGEOUS) {
                negativeArticles.add(article);
            }
        }

        return AnalyzedArticleResponse.of(positiveArticles, neutralArticles, negativeArticles);
    }
}
