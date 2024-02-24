package com.neupinion.neupinion.issue.application;

import com.neupinion.neupinion.issue.application.dto.FollowUpIssueByCategoryResponse;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueCreateRequest;
import com.neupinion.neupinion.issue.application.dto.FollowUpIssueResponse;
import com.neupinion.neupinion.issue.application.dto.UnviewedFollowUpIssueResponse;
import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.event.FollowUpIssueViewedEvent;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.ReprocessedIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.dto.FollowUpIssueWithReprocessedIssueTitle;
import com.neupinion.neupinion.issue.exception.FollowUpIssueException.FollowUpIssueNotFoundException;
import com.neupinion.neupinion.issue.exception.ReprocessedIssueException;
import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.repository.FollowUpIssueOpinionRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FollowUpIssueService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ReprocessedIssueRepository reprocessedIssueRepository;
    private final FollowUpIssueRepository followUpIssueRepository;
    private final FollowUpIssueOpinionRepository followUpIssueOpinionRepository;
    private final ApplicationEventPublisher publisher;

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
        final Map<Long, FollowUpIssueOpinion> opinionsByMember = followUpIssueOpinionRepository.findByMemberId(memberId).stream()
            .collect(Collectors.toMap(FollowUpIssueOpinion::getFollowUpIssueId, opinion -> opinion));

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
        final Map<Long, FollowUpIssueOpinion> opinionsByMember = followUpIssueOpinionRepository.findByMemberId(memberId).stream()
            .collect(Collectors.toMap(FollowUpIssueOpinion::getFollowUpIssueId, opinion -> opinion));

        return dtos.stream()
            .filter(dto -> opinionsByMember.containsKey(dto.getFollowUpIssue().getReprocessedIssueId()))
            .map(FollowUpIssueByCategoryResponse::createVotedResponse)
            .collect(Collectors.toList());
    }

    public FollowUpIssueResponse findById(final Long id, final Long memberId) {
        final FollowUpIssue followUpIssue = followUpIssueRepository.findById(id)
            .orElseThrow(FollowUpIssueNotFoundException::new);
        publisher.publishEvent(new FollowUpIssueViewedEvent(id, memberId));
        return FollowUpIssueResponse.from(followUpIssue);
    }

    public List<UnviewedFollowUpIssueResponse> findUnviewedSortByLatest(final Long memberId) {
        final List<FollowUpIssue> followUpIssues = followUpIssueRepository.findUnviewedSortByCreatedAt(memberId);

        return followUpIssues.stream()
            .map(UnviewedFollowUpIssueResponse::from)
            .toList();
    }
}

