package com.neupinion.neupinion.member.config;

import com.neupinion.neupinion.member.domain.DefaultNicknamePolicy;
import com.neupinion.neupinion.member.infrastructure.DefaultNicknamePolicyImpl;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultNicknamePolicyConfig {

    @Bean
    public DefaultNicknamePolicy defaultNicknamePolicy() {
        final List<String> adjectives = List.of(
            "행복한", "즐거운", "편안한", "품위있는", "장난스러운", "몰입하는", "용감한", "차분한", "노력하는", "신중한", "순수한", "명랑한", "대담한", "똑똑한",
            "유능한", "성실한", "사랑스러운", "적극적인", "호기심많은", "낙천적인", "낭만적인", "감성적인", "열정적인", "밝은", "긍정적인", "남다른", "유쾌한", "유연한",
            "정확한", "자애로운", "자유로운", "자신감있는", "자유분방한", "의논을 좋아하는", "당당한"
        );
        final List<String> nouns = List.of(
            "뉴피니언", "뉴피", "독자", "토론자", "탐험가", "모험가", "선구자", "해결사"
        );

        return new DefaultNicknamePolicyImpl(adjectives, nouns);
    }

}
