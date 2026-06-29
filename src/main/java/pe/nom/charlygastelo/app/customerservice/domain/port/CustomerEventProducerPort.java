package pe.nom.charlygastelo.app.customerservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;

public interface CustomerEventProducerPort {

    Completable publishCustomerCreated(Customer customer);

    Completable publishCustomerUpdated(Customer customer);

    Completable publishCustomerDeleted(String customerId);
}
