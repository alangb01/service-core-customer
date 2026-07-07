package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;

@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

    private final CustomerReactiveRepository repository;
    private final PersistenceMapper mapper;



    @Override
    public Single<Customer> save(Customer customer) {
        return Single.fromPublisher(
                repository.save(mapper.toCustomerDocument(customer))
                        .map(mapper::toCustomerDomain)
        );
    }

    @Override
    public Maybe<Customer> findById(String id) {
        return Maybe.fromPublisher(
                repository.findById(id)
                        .map(mapper::toCustomerDomain)
        );
    }

    @Override
    public Maybe<Customer> findByDocument(DocumentType documentType, String documentNumber) {
        return Maybe.fromPublisher(
                repository.findByDocumentTypeAndDocumentNumber(documentType, documentNumber)
                        .map(mapper::toCustomerDomain)
        );
    }

    @Override
    public Flowable<Customer> findAll() {
        return Flowable.fromPublisher(
                repository.findAll()
                        .map(mapper::toCustomerDomain)
        );
    }

    @Override
    public Completable deleteById(String id) {
        return Completable.fromPublisher(
                repository.deleteById(id)
        );
    }

    @Override
    public Single<Boolean> existsById(String id) {
        return Single.fromPublisher(
                repository.existsById(id)
        );
    }

    @Override
    public Single<Boolean> checkByCustomerTypeAndDocumentTypeAndDocumentNumber(
            CustomerType customerType,
            DocumentType documentType,
            String documentNumber
    ) {
        return Single.fromPublisher(
                repository.existsByCustomerTypeAndDocumentTypeAndDocumentNumber(
                        customerType,
                        documentType,
                        documentNumber
                )
        );
    }
}
