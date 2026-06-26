package pe.nom.charlygastelo.app.customerservice.infrastructure.events.mapper;


import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.infrastructure.avro.events.CustomerCreatedEvent;
import pe.nom.charlygastelo.app.customerservice.infrastructure.avro.events.CustomerUpdatedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerResponseEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;


@Component
public class CustomerEventMapper {

    public CustomerResponseEvent toCustomerResponseEvent(
            Customer customer,
            String correlationId) {

        return CustomerResponseEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CUSTOMER_RESPONSE")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("customer-service")
                .setCorrelationId(correlationId)
                .setFound(true)
                .setCustomerId(customer.id())
                .setCustomerType(valueOrEmpty(customer.customerType().toString()))
                .setDocumentType(customer.documentType().toString())
                .setDocumentNumber(customer.documentNumber())
                .setName(customer.name())
                .setLastName(valueOrEmpty(customer.lastName()))
                .setEmail(customer.email())
                .setPhone(customer.phone())
                .setActive(customer.active())
                .build();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    public CustomerResponseEvent toCustomerNotFoundEvent(
            String customerId,
            String correlationId) {

        return CustomerResponseEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CUSTOMER_RESPONSE")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("customer-service")
                .setCorrelationId(correlationId)
                .setFound(false)
                .setCustomerId(customerId)
                .setCustomerType("")
                .setDocumentType("")
                .setDocumentNumber("")
                .setName("")
                .setLastName("")
                .setEmail("")
                .setPhone("")
                .setActive(false)
                .build();
    }

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
