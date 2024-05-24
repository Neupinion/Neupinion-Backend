package com.neupinion.neupinion.member.infrastructure;

import com.neupinion.neupinion.member.domain.DefaultNicknamePolicy;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultNicknamePolicyImpl implements DefaultNicknamePolicy {

    private final List<String> adjectives;
    private final List<String> nouns;

    @Override
    public String generate() {
        final Random random = new Random();
        final String adjective = adjectives.get(random.nextInt(adjectives.size()));
        final String noun = nouns.get(random.nextInt(nouns.size()));

        return adjective + " " + noun;
    }
}
