package com.neupinion.neupinion.member.domain;

import com.neupinion.neupinion.auth.application.OAuthType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Nickname nickname;

    @Column(name = "profile_image_url", nullable = false)
    private String profileImageUrl;

    @Column(name = "auth_type")
    @Enumerated(EnumType.STRING)
    private OAuthType authType;

    @Column(name = "auth_key")
    private String authKey;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
    }

    public Member(final Long id, final String nickname, final String profileImageUrl, final OAuthType authType,
                  final String authKey) {
        this.id = id;
        this.nickname = new Nickname(nickname);
        this.profileImageUrl = profileImageUrl;
        this.authType = authType;
        this.authKey = authKey;
    }

    public static Member forSave(final String nickname, final String profileImageUrl) {
        return new Member(null, nickname, profileImageUrl, null, null);
    }

    public static Member forSave(final String nickname, final String profileImageUrl, final OAuthType authType,
                                 final String authKey) {
        return new Member(null, nickname, profileImageUrl, authType, authKey);
    }

    public String getNickname() {
        return nickname.getValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Member member = (Member) o;
        if (Objects.isNull(this.id) || Objects.isNull(member.id)) {
            return false;
        }
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
