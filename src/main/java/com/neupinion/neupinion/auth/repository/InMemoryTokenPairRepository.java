package com.neupinion.neupinion.auth.repository;

import com.neupinion.neupinion.auth.exception.TokenException;
import com.neupinion.neupinion.auth.exception.TokenException.TokenPairNotMatchingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Getter
@Repository
public class InMemoryTokenPairRepository {

    private final Map<String, String> tokenPairs = new ConcurrentHashMap<>();

    public void validateTokenPair(final String refreshToken, final String accessToken) {
        if (!tokenPairs.containsKey(refreshToken)) {
            throw new TokenException.RefreshTokenNotFoundException(Map.of("wrongRefreshToken", refreshToken));
        }
        if (!tokenPairs.get(refreshToken).equals(accessToken)) {
            throw new TokenPairNotMatchingException(Map.of("wrongAccessToken", accessToken));
        }
    }

    public void addOrUpdateTokenPair(final String refreshToken, final String accessToken) {
        tokenPairs.put(refreshToken, accessToken);
    }

    public void delete(final String refreshToken) {
        tokenPairs.remove(refreshToken);
    }

    public void clear() {
        tokenPairs.clear();
    }
}
