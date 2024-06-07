package com.neupinion.neupinion.issue.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class RelatableStand {

    public static final RelatableStand NOT_VOTED = new RelatableStand(0L, false, 0L, false);

    @Column(name = "first_stand_id", nullable = false)
    private Long firstStandId;

    @Column(name = "first_relatable", nullable = false)
    private boolean firstRelatable;

    @Column(name = "second_stand_id", nullable = false)
    private Long secondStandId;

    @Column(name = "second_relatable", nullable = false)
    private boolean secondRelatable;

    public RelatableStand(final Long firstStandId, final boolean firstRelatable, final Long secondStandId,
                          final boolean secondRelatable) {
        this.firstStandId = firstStandId;
        this.firstRelatable = firstRelatable;
        this.secondStandId = secondStandId;
        this.secondRelatable = secondRelatable;
    }
}
