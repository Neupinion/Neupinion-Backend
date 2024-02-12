package com.neupinion.neupinion.issue.domain.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import com.neupinion.neupinion.issue.domain.FollowUpIssueViews;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueRepository;
import com.neupinion.neupinion.issue.domain.repository.FollowUpIssueViewsRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class ViewsListenerTest {

    @Mock
    private FollowUpIssueRepository followUpIssueRepository;

    @Mock
    private FollowUpIssueViewsRepository followUpIssueViewsRepository;

    private ViewsListener viewsListener;

    @BeforeEach
    void setUp() {
        viewsListener = new ViewsListener(followUpIssueRepository, followUpIssueViewsRepository);
    }

    @Test
    void 후속_이슈_조회_객체를_생성하고_후속_이슈의_조회수를_1_증가시킨다() {
        // given
        final Long followUpIssueId = 1L;
        final Long reprocessedIssueId = 1L;
        final long memberId = 2L;
        final FollowUpIssueViewedEvent event = new FollowUpIssueViewedEvent(followUpIssueId, memberId);

        // when
        when(followUpIssueViewsRepository.existsByFollowUpIssueIdAndMemberId(followUpIssueId,
                                                                             memberId)).thenReturn(false);
        when(followUpIssueRepository.findById(followUpIssueId)).thenReturn(
            Optional.of(FollowUpIssue.forSave("제목", "image", Category.ECONOMY, FollowUpIssueTag.INTERVIEW,
                                              reprocessedIssueId)));

        viewsListener.onFollowUpIssueViewedEvent(event);

        // then
        verify(followUpIssueViewsRepository).save(any(FollowUpIssueViews.class));
        verify(followUpIssueRepository).findById(followUpIssueId);
    }

    @Test
    void 유저가_해당_후속_이슈를_조회했었다면_아무것도_하지_않는다() {
        // given
        final Long followUpIssueId = 1L;
        final long memberId = 2L;
        final FollowUpIssueViewedEvent event = new FollowUpIssueViewedEvent(followUpIssueId, memberId);

        // when
        when(followUpIssueViewsRepository.existsByFollowUpIssueIdAndMemberId(followUpIssueId,
                                                                             memberId)).thenReturn(true);

        viewsListener.onFollowUpIssueViewedEvent(event);

        // then
        verify(followUpIssueViewsRepository, never()).save(any(FollowUpIssueViews.class));
        verify(followUpIssueRepository, never()).findById(followUpIssueId);
    }
}
