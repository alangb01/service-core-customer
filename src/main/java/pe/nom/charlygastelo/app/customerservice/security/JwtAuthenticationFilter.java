package pe.nom.charlygastelo.app.customerservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import lombok.RequiredArgsConstructor;
import reactor.adapter.rxjava.RxJava3Adapter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenValidator jwtValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String requestId = UUID.randomUUID().toString();
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        log.info("[AUTH-FILTER] IN → requestId={} method={} path={}", requestId, method, path);

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("[AUTH-FILTER] requestId={} No Bearer token found", requestId);
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        log.debug("[AUTH-FILTER] requestId={} Token received", requestId);

        return RxJava3Adapter.singleToMono(jwtValidator.validate(token))
                .doOnSubscribe(sub -> log.debug("[AUTH-FILTER] requestId={} Validating JWT…", requestId))
                .doOnError(err -> log.error("[AUTH-FILTER] requestId={} JWT validation failed: {}", requestId, err.getMessage()))
                .flatMap(user -> {

                    log.debug("[AUTH-FILTER] requestId={} JWT valid → user={} customer={} roles={}",
                            requestId,
                            user.userId(),
                            user.customerId(),
                            user.roles());

                    List<GrantedAuthority> authorities = toAuthorities(user.roles());
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    authorities
                            );

                    SecurityContext context = new SecurityContextImpl(auth);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                })
                .onErrorResume(e -> chain.filter(exchange));

    }

    private List<GrantedAuthority> toAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}