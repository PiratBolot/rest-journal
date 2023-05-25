package ru.university.mikita.restjournal.login.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import ru.university.mikita.restjournal.login.security.service.UserDetailsImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${mikita.app.jwtSecret}")
    private String jwtSecret;

    @Value("${mikita.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${mikita.app.jwtCookieName}")
    private String jwtCookie;

    @Nullable
    public String getJwtFromCookies(final HttpServletRequest request) {
        final Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public ResponseCookie generateJwtCookie(final UserDetailsImpl userPrincipal) {
        final String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        final ResponseCookie cookie =
                ResponseCookie.from(jwtCookie, jwt)
                              .path("/api")
                              .maxAge(Duration.ofDays(1).getSeconds())
                              .httpOnly(true)
                              .build();
        return cookie;
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null).path("/api").build();
    }

    public String getUserNameFromJwtToken(final String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(final String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String generateTokenFromUsername(final String username) {
        return Jwts.builder()
                   .setSubject(username)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                   .signWith(SignatureAlgorithm.HS512, jwtSecret)
                   .compact();
    }
}
