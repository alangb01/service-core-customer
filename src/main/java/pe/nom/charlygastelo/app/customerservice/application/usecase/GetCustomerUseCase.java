package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.exception.CustomerRepositoryException;

@RequiredArgsConstructor
@Slf4j
public class GetCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerCachePort cache;

    public Maybe<Customer> byId(String id) {

        log.info("Searching customer by ID {}", id);

        return cache.getById(id)
                .doOnSuccess(c -> log.info("Customer {} found in cache", id))
                .doOnComplete(() -> log.debug("Customer {} not found in cache", id))

                .switchIfEmpty(
                        customerRepository.findById(id)
                                .doOnSuccess(c -> log.info("Customer {} found in MongoDB", id))
                                .doOnError(e -> log.error("Error accessing MongoDB for {}", id, e))
                                .flatMap(customer ->
                                        cache.save(customer)
                                                .doOnComplete(() -> log.debug("Customer {} cached", id))
                                                .andThen(Maybe.just(customer))
                                )
                )

                .switchIfEmpty(
                        Maybe.error(new CustomerNotFoundException("Customer not found: " + id))
                );
    }

    public Maybe<Customer> byDocument(String type, String number) {

        log.info("Searching customer by document {} {}", type, number);

        return cache.getByDocument(type, number)
                .doOnSuccess(c -> log.info("Customer {}-{} found in cache", type, number))
                .doOnComplete(() -> log.debug("Customer {}-{} not found in cache", type, number))

                .switchIfEmpty(
                        customerRepository.findByDocument(type, number)
                                .doOnSuccess(c -> log.info("Customer {}-{} found in MongoDB", type, number))
                                .doOnError(e -> log.error("Error accessing MongoDB for {}-{}", type, number, e))
                                .flatMap(customer ->
                                        cache.save(customer)
                                                .doOnComplete(() -> log.debug("Customer {}-{} cached", type, number))
                                                .andThen(Maybe.just(customer))
                                )
                )

                .onErrorResumeNext(e -> {
                    log.error("Technical error retrieving customer {}-{}: {}", type, number, e.getMessage(), e);
                    return Maybe.error(new CustomerRepositoryException("Error accessing Mongo", e));
                })

                .switchIfEmpty(
                        Maybe.error(new CustomerNotFoundException(
                                "Customer not found with " + type + " " + number
                        ))
                );
    }

}