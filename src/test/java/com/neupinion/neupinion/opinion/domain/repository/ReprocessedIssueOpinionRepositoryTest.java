package com.neupinion.neupinion.opinion.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ReprocessedIssueOpinionRepositoryTest extends JpaRepositoryTest {

    @Autowired
    private ReprocessedIssueOpinionRepository reprocessedIssueOpinionRepository;

    @Test
    void 이미_동일한_문단에_등록된_의견인지_확인한다() {
        // given
        final long memberId = 1L;
        final long paragraphId = 1L;
        final long issueId = 1L;

        reprocessedIssueOpinionRepository.save(ReprocessedIssueOpinion.forSave(paragraphId, issueId, memberId, "내용"));

        saveAndClearEntityManager();

        // when
        final boolean isExisted = reprocessedIssueOpinionRepository.existsByMemberIdAndParagraphId(memberId,
                                                                                                   paragraphId);

        // then
        assertThat(isExisted).isTrue();
    }
}
