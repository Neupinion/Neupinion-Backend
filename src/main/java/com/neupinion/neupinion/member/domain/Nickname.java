package com.neupinion.neupinion.member.domain;

import com.neupinion.neupinion.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Nickname {

    private static final int MAXIMUM_LENGTH = 30;
    @Column(name = "nickname", nullable = false)
    private String value;

    public Nickname(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (value.length() > MAXIMUM_LENGTH) {
            throw new MemberException.NicknameLengthExceededException(Map.of("wrongNickname", value));
        }
    }
}
