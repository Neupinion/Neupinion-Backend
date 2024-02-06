package com.neupinion.neupinion.issue.application;

import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.Opinion;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.OpinionRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.dto.FollowUpIssueWithReprocessedIssueTitle;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FollowUpIssueService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ReprocessedIssueRepository reprocessedIssueRepository;
    private final FollowUpIssueRepository followUpIssueRepository;
    private final OpinionRepository opinionRepository;

    @Transactional
    public Long createFollowUpIssue(final FollowUpIssueCreateRequest request) {
        validateReprocessedIssue(request.getReprocessedIssueId());
        final FollowUpIssue savedFollowUpIssue = followUpIssueRepository.save(request.toEntity());

        return savedFollowUpIssue.getId();
    }

    private void validateReprocessedIssue(final Long reprocessedIssueId) {
        reprocessedIssueRepository.findById(reprocessedIssueId)
            .orElseThrow(ReprocessedIssueException.ReprocessedIssueNotFoundException::new);
    }

    public List<FollowUpIssueByCategoryResponse> findFollowUpIssueByCategoryAndDate(final String dateFormat,
                                                                                    final String category,
                                                                                    final Long memberId) {
        final LocalDate targetDate = LocalDate.parse(dateFormat, FORMATTER);
        final List<FollowUpIssueWithReprocessedIssueTitle> dtos = followUpIssueRepository.findByCategoryAndDate(
            Category.from(category), targetDate);
        final Map<Long, Opinion> opinionsByMember = opinionRepository.findByMemberId(memberId).stream()
            .collect(Collectors.toMap(Opinion::getIssueId, opinion -> opinion));

        final List<FollowUpIssueByCategoryResponse> result = new ArrayList<>();
        for (final FollowUpIssueWithReprocessedIssueTitle dto : dtos) {
            final FollowUpIssue followUpIssue = dto.getFollowUpIssue();
            if (opinionsByMember.containsKey(followUpIssue.getReprocessedIssueId())) {
                result.add(FollowUpIssueByCategoryResponse.createNotVotedResponse(dto));
                continue;
            }
            result.add(FollowUpIssueByCategoryResponse.createVotedResponse(dto));
        }

        return result;
    }

    public List<FollowUpIssueByCategoryResponse> findMyVotedIssueByCategoryAndDate(final String dateFormat,
                                                                                   final String category,
                                                                                   final Long memberId) {
        final LocalDate targetDate = LocalDate.parse(dateFormat, FORMATTER);
        final List<FollowUpIssueWithReprocessedIssueTitle> dtos = followUpIssueRepository.findByCategoryAndDate(
            Category.from(category), targetDate);
        final Map<Long, Opinion> opinionsByMember = opinionRepository.findByMemberId(memberId).stream()
            .collect(Collectors.toMap(Opinion::getIssueId, opinion -> opinion));

        return dtos.stream()
            .filter(dto -> opinionsByMember.containsKey(dto.getFollowUpIssue().getReprocessedIssueId()))
            .map(FollowUpIssueByCategoryResponse::createVotedResponse)
            .collect(Collectors.toList());
    }
}

