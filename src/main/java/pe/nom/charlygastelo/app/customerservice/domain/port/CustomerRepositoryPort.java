package pe.nom.charlygastelo.app.customerservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;

public interface CustomerRepositoryPort {

    Single<Customer> save(Customer customer);

    Maybe<Customer> findById(String id);

    Maybe<Customer> findByDocument(String documentType, String documentNumber);

    Flowable<Customer> findAll();

    Completable deleteById(String id);

    Single<Boolean> existsById(String id);

    Single<Boolean> checkByCustomerTypeAndDocumentTypeAndDocumentNumber(
            String customerType,
            String documentType,
            String documentNumber
    );
}