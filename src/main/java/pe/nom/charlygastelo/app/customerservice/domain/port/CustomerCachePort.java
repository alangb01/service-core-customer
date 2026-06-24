package pe.nom.charlygastelo.app.customerservice.domain.port;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;

public interface CustomerCachePort {

    Maybe<Customer> getById(String id);

    Maybe<Customer> getByDocument(String type,String number);

    Completable save(Customer customer);

    Completable delete(String id);
}
