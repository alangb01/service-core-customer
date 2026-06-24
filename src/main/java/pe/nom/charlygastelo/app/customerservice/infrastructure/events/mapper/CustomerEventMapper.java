package pe.nom.charlygastelo.app.customerservice.infrastructure.events.mapper;


import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.infrastructure.avro.events.CustomerCreatedEvent;
import pe.nom.charlygastelo.app.customerservice.infrastructure.avro.events.CustomerUpdatedEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;


@Component
public class CustomerEventMapper {

    public Object toCustomerCreatedEvent(Customer domain) {
        return new CustomerCreatedEvent(
                domain.id(),
                domain.customerType().toString(),
                domain.documentType().toString(),
                domain.documentNumber(),
                domain.name(),
                domain.lastName(),
                domain.email(),
                domain.phone(),
                Instant.now().getEpochSecond()
        );
    }

    public Object toCustomerUpdatedEvent(Customer domain) {
        return new CustomerUpdatedEvent(
                domain.id(),
                domain.customerType().toString(),
                domain.documentType().toString(),
                domain.documentNumber(),
                domain.name(),
                domain.lastName(),
                domain.email(),
                domain.phone(),
                Instant.now().getEpochSecond()
        );
    }
}
