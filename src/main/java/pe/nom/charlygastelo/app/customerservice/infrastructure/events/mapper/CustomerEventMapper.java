package pe.nom.charlygastelo.app.customerservice.infrastructure.events.mapper;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerCreatedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerDeletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerResponseEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerUpdatedEvent;

@Component
public class CustomerEventMapper {

    public CustomerCreatedEvent toCustomerCreatedEvent(Customer customer) {
        return CustomerCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CUSTOMER_CREATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("customer-service")
                .setCustomerId(value(customer.id()))
                .setCustomerType(customer.customerType().name())
                .setDocumentType(customer.documentType().name())
                .setDocumentNumber(value(customer.documentNumber()))
                .setProfileType(customer.profileType().name())
                .setName(value(customer.name()))
                .setLastName(value(customer.lastName()))
                .setEmail(value(customer.email()))
                .setPhone(value(customer.phone()))
                .setActive(customer.active())
                .build();
    }

    public CustomerUpdatedEvent toCustomerUpdatedEvent(Customer customer) {
        return CustomerUpdatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CUSTOMER_UPDATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("customer-service")
                .setCustomerId(value(customer.id()))
                .setCustomerType(customer.customerType().name())
                .setDocumentType(customer.documentType().name())
                .setDocumentNumber(value(customer.documentNumber()))
                .setProfileType(customer.profileType().name())
                .setName(value(customer.name()))
                .setLastName(value(customer.lastName()))
                .setEmail(value(customer.email()))
                .setPhone(value(customer.phone()))
                .setActive(customer.active())
                .build();
    }

    public CustomerDeletedEvent toCustomerDeletedEvent(String customerId) {
        return CustomerDeletedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CUSTOMER_DELETED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("customer-service")
                .setCustomerId(value(customerId))
                .build();
    }


    public CustomerResponseEvent toCustomerResponseEvent(
            Customer customer,
            String correlationId) {

        return CustomerResponseEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CUSTOMER_RESPONSE")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("customer-service")
                .setCorrelationId(value(correlationId))
                .setFound(true)
                .setCustomerId(value(customer.id()))
                .setCustomerType(customer.customerType().name())
                .setDocumentType(customer.documentType().name())
                .setDocumentNumber(value(customer.documentNumber()))
                .setName(value(customer.name()))
                .setLastName(value(customer.lastName()))
                .setEmail(value(customer.email()))
                .setPhone(value(customer.phone()))
                .setActive(customer.active())
                .build();
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
                .setCorrelationId(value(correlationId))
                .setFound(false)
                .setCustomerId(value(customerId))
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

    private String value(String value) {
        return value == null ? "" : value;
    }
}