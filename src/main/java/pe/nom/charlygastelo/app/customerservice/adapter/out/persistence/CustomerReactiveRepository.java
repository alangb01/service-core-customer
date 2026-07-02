package pe.nom.charlygastelo.app.customerservice.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerReactiveRepository
        extends ReactiveMongoRepository<CustomerDocument, String> {

    Mono<CustomerDocument> findByDocumentTypeAndDocumentNumber(
            DocumentType documentType,
            String documentNumber
    );

    Mono<Boolean> existsByCustomerTypeAndDocumentTypeAndDocumentNumber(
            CustomerType customerType,
            DocumentType documentType,
            String documentNumber
    );
}
