package pe.nom.charlygastelo.app.customerservice.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.reactivex.rxjava3.core.Single;

@Service
public class JwtTokenValidator {

    @Value("${jwt.secret}")
    private String secret;

    public Single<UserPrincipal> validate(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String customerId = claims.getSubject();
        String userId=claims.get("userId", String.class); // ✔ tu token usa "userId")
        List<String> roles = claims.get("roles", List.class); // ✔ tu token usa "roles"

        return Single.just(new UserPrincipal(userId, customerId, roles));
    }
}