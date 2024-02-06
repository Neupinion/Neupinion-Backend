package com.neupinion.neupinion.issue.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.neupinion.neupinion.issue.domain.Opinion;
import com.neupinion.neupinion.utils.JpaRepositoryTest;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class OpinionRepositoryTest extends JpaRepositoryTest {

    @Autowired
    private OpinionRepository opinionRepository;

    @Test
    void 멤버의_id로_Opinion을_조회한다() {
        // given
        final long targetMemberId = 1L;
        final long otherMemberId = 2L;
        final Opinion opinion1 = opinionRepository.save(Opinion.forSave(1L, targetMemberId));
        final Opinion opinion2 = opinionRepository.save(Opinion.forSave(2L, targetMemberId));
        final Opinion opinion3 = opinionRepository.save(Opinion.forSave(3L, targetMemberId));
        final Opinion opinion4 = opinionRepository.save(Opinion.forSave(4L, targetMemberId));
        opinionRepository.save(Opinion.forSave(1L, otherMemberId));
        opinionRepository.save(Opinion.forSave(2L, otherMemberId));
        opinionRepository.save(Opinion.forSave(3L, otherMemberId));

        saveAndClearEntityManager();

        // when
        final Set<Opinion> opinions = opinionRepository.findByMemberId(targetMemberId);

        // then
        assertAll(
            () -> assertThat(opinions).hasSize(4),
            () -> assertThat(opinions).allMatch(opinion -> opinion.getMemberId().equals(targetMemberId)),
            () -> assertThat(opinions).containsExactlyInAnyOrder(opinion1, opinion2, opinion3, opinion4)
        );
    }
}
