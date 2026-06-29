package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerAlreadyExistsException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventProducerPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class CreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerEventProducerPort producer;
    private final CustomerCachePort cache;

    public Single<Customer> execute(Customer customer) {
        log.info("[CUSTOMER-CREATE] Starting creation process. documentNumber={}",
                customer.documentNumber());

        return customerRepository
                .checkByCustomerTypeAndDocumentTypeAndDocumentNumber(
                        customer.customerType().toString(),
                        customer.documentType().toString(),
                        customer.documentNumber()
                )
                .flatMap(exists -> {
                    if (exists) {
                        return Single.error(new CustomerAlreadyExistsException(
                                "documentNumber=" + customer.documentNumber()
                        ));
                    }

                    return customerRepository.save(customer);
                })
                .flatMap(saved ->
                        safeCacheSave(saved)
                                .andThen(Single.just(saved))
                )
                .flatMap(saved ->
                        producer.publishCustomerCreated(saved)
                                .andThen(Single.just(saved))
                )
                .doOnSuccess(saved ->
                        log.info("[CUSTOMER-CREATE] Customer created successfully. id={}", saved.id()))
                .doOnError(e ->
                        log.error("[CUSTOMER-CREATE] Error during creation. documentNumber={}, reason={}",
                                customer.documentNumber(), e.getMessage(), e));
    }

    private Completable safeCacheSave(Customer customer) {
        Completable op = cache.save(customer);

        if (op == null) {
            log.warn("[CUSTOMER-CREATE] Cache save returned null. id={}", customer.id());
            return Completable.complete();
        }

        return op.onErrorComplete(e -> {
            log.warn("[CUSTOMER-CREATE] Cache save failed. Continuing without cache. id={}, reason={}",
                    customer.id(), e.getMessage());
            return true;
        });
    }
}