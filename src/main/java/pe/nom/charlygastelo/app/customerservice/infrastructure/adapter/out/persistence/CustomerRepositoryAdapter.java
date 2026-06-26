package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.ReactiveCustomerRepository;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;

@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

    private final ReactiveCustomerRepository repository;
    private final PersistenceMapper mapper;



    @Override
    public Single<Customer> save(Customer customer) {
        return Single.fromPublisher(
                repository.save(mapper.toDocument(customer))
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Maybe<Customer> findById(String id) {
        return Maybe.fromPublisher(
                repository.findById(id)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Maybe<Customer> findByDocument(String documentType, String documentNumber) {
        return Maybe.fromPublisher(
                repository.findByDocumentTypeAndDocumentNumber(documentType, documentNumber)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Flowable<Customer> findAll() {
        return Flowable.fromPublisher(
                repository.findAll()
                        .map(mapper::toDomain)
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
            String customerType,
            String documentType,
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
