package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReactiveCustomerRepository
        extends ReactiveMongoRepository<CustomerDocument, String> {

    Mono<CustomerDocument> findByDocumentTypeAndDocumentNumber(
            String documentType,
            String documentNumber
    );

    Mono<Boolean> existsByCustomerTypeAndDocumentTypeAndDocumentNumber(
            String customerType,
            String documentType,
            String documentNumber
    );
}
