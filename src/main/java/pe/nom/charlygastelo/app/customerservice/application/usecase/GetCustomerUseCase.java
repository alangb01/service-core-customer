package pe.nom.charlygastelo.app.customerservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.application.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.exception.CustomerRepositoryException;

@RequiredArgsConstructor
public class GetCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerCachePort cache;

    public Maybe<Customer> byId(String id) {

//        return customerRepository.findById(id);
        return cache.getById(id)
                .switchIfEmpty(
                        customerRepository.findById(id)
                                .flatMap(customer ->
                                        cache.save(customer).andThen(Maybe.just(customer))
                                )
                );
    }

    public Maybe<Customer> byDocument(String type, String number) {
        return cache.getByDocument(type, number)

                // Si no está en cache → buscar en Mongo
                .switchIfEmpty(
                        customerRepository.findByDocument(type, number)
                                .flatMap(customer ->
                                        cache.save(customer).andThen(Maybe.just(customer))
                                )
                )

                // Si Mongo falla → error técnico → convertir a excepción de infraestructura
                .onErrorResumeNext(e ->
                        Maybe.error(new CustomerRepositoryException("Error accessing Mongo", e))
                )

                // Si no existe → error de aplicación
                .switchIfEmpty(
                        Maybe.error(new CustomerNotFoundException(
                                "Customer not found with " + type + " " + number
                        ))
                );
    }
}