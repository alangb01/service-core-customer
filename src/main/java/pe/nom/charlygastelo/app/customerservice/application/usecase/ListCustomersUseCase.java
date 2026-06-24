package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class ListCustomersUseCase {

    private final CustomerRepositoryPort customerRepository;

    public Flowable<Customer> all() {

        log.info("Listing all customers");

        return customerRepository.findAll()
                .doOnSubscribe(s -> log.debug("Starting MongoDB findAll()"))
                .doOnNext(c -> log.debug("Customer loaded: {}", c.id()))
                .doOnComplete(() -> log.info("All customers loaded successfully"))
                .doOnError(e -> log.error("Error loading customers: {}", e.getMessage(), e));
    }
}