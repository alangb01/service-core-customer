package pe.nom.charlygastelo.app.customerservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;

public interface CustomerEventPort {
    Completable publishCustomerCreatedEvent(Customer customer);

    Completable publishCustomerUpdatedEvent(Customer customer);
}
