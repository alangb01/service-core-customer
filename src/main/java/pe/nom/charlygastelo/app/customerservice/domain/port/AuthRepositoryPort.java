package pe.nom.charlygastelo.app.customerservice.domain.port;


import pe.nom.charlygastelo.app.customerservice.domain.model.ValidateResponse;
import reactor.core.publisher.Mono;

public interface AuthRepositoryPort {
    Mono<ValidateResponse> validate(String token);
}
