package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class ListCustomersUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerCachePort cache;

    public Flowable<Customer> all() {
        log.info("[CUSTOMER-LIST] Listing all customers");

        return customerRepository.findAll()
                .flatMap(customer ->
                        safeCacheSave(customer)
                                .andThen(Flowable.just(customer))
                )
                .doOnNext(c ->
                        log.debug("[CUSTOMER-LIST] Customer loaded: {}", c.id()))
                .doOnComplete(() ->
                        log.info("[CUSTOMER-LIST] All customers loaded successfully"))
                .doOnError(e ->
                        log.error("[CUSTOMER-LIST] Error loading customers: {}", e.getMessage(), e));
    }

    private Completable safeCacheSave(Customer customer) {
        Completable op = cache.save(customer);

        if (op == null) {
            log.warn("[CUSTOMER-LIST] Cache save returned null. Skipping. customerId={}", customer.id());
            return Completable.complete();
        }

        return op.onErrorComplete(e -> {
            log.warn("[CUSTOMER-LIST] Cache save failed. Continuing without cache. customerId={}, reason={}",
                    customer.id(), e.getMessage());
            return true;
        });
    }
}