package com.sp.refreshtoken.security.service;

import com.sp.refreshtoken.entity.app.RefreshToken;
import com.sp.refreshtoken.entity.app.User;
import com.sp.refreshtoken.exception.TokenRefreshException;
import com.sp.refreshtoken.repository.RefreshTokenRepository;
import com.sp.refreshtoken.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expiration-ms}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken createRefreshToken(String userId) {
        User user = userRepository.findById(userId).get();

        Optional<RefreshToken> oldRefreshToken = refreshTokenRepository.findByUser(user);
        if(oldRefreshToken.isPresent()){
            refreshTokenRepository.delete(oldRefreshToken.get());
//            int i = refreshTokenRepository.deleteByUser(user);
//            System.out.println("number of deleted item: "+i);
        }

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        System.out.println("------------before save refresh token----------");
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }
}