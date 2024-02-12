package com.neupinion.neupinion.issue.domain.event;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueViews;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueViewsRepository;
import com.neupinion.neupinion.issue.exception.FollowUpIssueException.FollowUpIssueNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ViewsListener {

    private final FollowUpIssueRepository followUpIssueRepository;
    private final FollowUpIssueViewsRepository followUpIssueViewsRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFollowUpIssueViewedEvent(final FollowUpIssueViewedEvent followUpIssueViewedEvent) {
        final boolean isExisted = followUpIssueViewsRepository.existsByFollowUpIssueIdAndMemberId(
            followUpIssueViewedEvent.followUpIssueId(),
            followUpIssueViewedEvent.memberId());
        if (!isExisted) {
            saveFollowUpIssueViews(followUpIssueViewedEvent);
            viewFollowUpIssue(followUpIssueViewedEvent);
        }
    }

    private void viewFollowUpIssue(final FollowUpIssueViewedEvent followUpIssueViewedEvent) {
        final FollowUpIssue followUpIssue = followUpIssueRepository.findById(followUpIssueViewedEvent.followUpIssueId())
            .orElseThrow(FollowUpIssueNotFoundException::new);
        followUpIssue.view();
    }

    private void saveFollowUpIssueViews(final FollowUpIssueViewedEvent followUpIssueViewedEvent) {
        final FollowUpIssueViews followUpIssueViews = FollowUpIssueViews.of(followUpIssueViewedEvent.followUpIssueId(),
                                                                            followUpIssueViewedEvent.memberId());
        followUpIssueViewsRepository.save(followUpIssueViews);
    }
}
