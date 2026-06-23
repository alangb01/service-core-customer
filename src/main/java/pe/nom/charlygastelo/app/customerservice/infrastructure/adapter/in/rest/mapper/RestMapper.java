package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.mapper;

import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.CreateCustomerRequest;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.CustomerResponse;

@Component
public class RestMapper {
    public Customer toDomain(CreateCustomerRequest request) {
        return new Customer(
                null,
                CustomerType.valueOf(request.customerType()),
                DocumentType.valueOf(request.documentType()),
                request.documentNumber(),
                request.name(),
                request.lastName(),
                request.email(),
                request.phone(),
                request.active()

        );
    }

    public CustomerResponse toResponse(Customer domain) {
        return new CustomerResponse(
                domain.id(),
                domain.customerType().name(),
                domain.documentType().name(),
                domain.documentNumber(),
                domain.name(),
                domain.lastName(),
                domain.email(),
                domain.phone(),
                domain.active()
        );
    }
}
