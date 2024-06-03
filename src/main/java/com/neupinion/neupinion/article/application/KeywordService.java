package com.neupinion.neupinion.article.application;

import com.neupinion.neupinion.article.application.dto.KeywordResponse;
import com.neupinion.neupinion.article.domain.IssueKeyword;
import com.neupinion.neupinion.article.domain.KeywordType;
import com.neupinion.neupinion.article.domain.repository.IssueKeywordRepository;
import com.neupinion.neupinion.issue.domain.IssueStand;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import com.neupinion.neupinion.issue.domain.repository.IssueStandRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueParagraphRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class KeywordService {

    private final ChatGptService chatGptService;
    private final IssueKeywordRepository issueKeywordRepository;
    private final ReprocessedIssueParagraphRepository reprocessedIssueParagraphRepository;
    private final IssueStandRepository issueStandRepository;

    @Transactional
    public KeywordResponse getKeywords(final Long issueId) {
        final List<IssueKeyword> keywords = issueKeywordRepository.findByIssueId(issueId);
        if (keywords.isEmpty()) {
            return extractKeyword(issueId);
        }
        return new KeywordResponse(
            keywords.stream()
                    .filter(keyword -> keyword.getType() == KeywordType.POSITIVE)
                    .map(IssueKeyword::getKeyword)
                    .toList(),
            keywords.stream()
                    .filter(keyword -> keyword.getType() == KeywordType.NEGATIVE)
                    .map(IssueKeyword::getKeyword)
                    .toList());
    }

    private KeywordResponse extractKeyword(final Long issueId) {
        final String articleBody = reprocessedIssueParagraphRepository.findByReprocessedIssueIdOrderById(issueId)
                                                                      .stream()
                                                                      .map(ReprocessedIssueParagraph::getContent)
                                                                      .collect(Collectors.joining("\n"));
        final List<IssueStand> stands = issueStandRepository.findByIssueIdOrderById(issueId);
        final KeywordResponse response = chatGptService.getKeywords(articleBody, stands, stands.get(0))
                                                       .block();
        response.getPositiveKeywords().forEach(keyword -> issueKeywordRepository.save(
            IssueKeyword.forSave(KeywordType.POSITIVE, keyword, issueId)
        ));
        response.getNegativeKeywords().forEach(keyword -> issueKeywordRepository.save(
            IssueKeyword.forSave(KeywordType.NEGATIVE, keyword, issueId)
        ));

        return response;
    }
}
