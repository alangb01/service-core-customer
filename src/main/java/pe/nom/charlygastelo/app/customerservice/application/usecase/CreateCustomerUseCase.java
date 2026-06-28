package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerAlreadyExistsException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventProducerPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;


@RequiredArgsConstructor
@Slf4j
public class CreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerEventProducerPort producer;

    public Single<Customer> execute(Customer customer) {

        log.info("Starting customer creation process for document {}", customer.documentNumber());

        return customerRepository
                .checkByCustomerTypeAndDocumentTypeAndDocumentNumber(
                        customer.customerType().toString(),
                        customer.documentType().toString(),
                        customer.documentNumber()
                )
                .doOnSuccess(exists ->
                        log.debug("Existence check for {} returned {}", customer.documentNumber(), exists)
                )

                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Customer already exists with document {}", customer.documentNumber());
                        return Single.error(new CustomerAlreadyExistsException(customer.documentNumber()));
                    }

                    log.info("Customer does not exist. Proceeding to save {}", customer.documentNumber());
                    return customerRepository.save(customer);
                })

                .doOnSuccess(saved ->
                        log.info("Customer {} saved successfully with ID {}", saved.documentNumber(), saved.id())
                )

                .flatMap(saved ->
                        producer.publishCustomerCreated(saved)
                                .doOnComplete(() ->
                                        log.info("CustomerCreatedEvent published for {}", saved.documentNumber())
                                )
                                .doOnError(e ->
                                        log.error("Error publishing event for {}: {}", saved.documentNumber(), e.getMessage(), e)
                                )
                                .andThen(Single.just(saved))
                )

                .doOnError(e ->
                        log.error("Error creating customer {}: {}", customer.documentNumber(), e.getMessage(), e)
                );
    }
}