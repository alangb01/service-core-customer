package pe.nom.charlygastelo.app.customerservice.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.customerservice.adapter.out.persistence.CustomerDocument;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.domain.model.ProfileType;

@Component
public class PersistenceMapper {
    public CustomerDocument toCustomerDocument(Customer domain) {
        CustomerDocument document = new CustomerDocument();
        document.setId(domain.id());
        document.setCustomerType(domain.customerType());
        document.setDocumentType(domain.documentType());
        document.setDocumentNumber(domain.documentNumber());
        document.setName(domain.name());
        document.setLastName(domain.lastName());
        document.setEmail(domain.email());
        document.setPhone(domain.phone());
        document.setActive(domain.active());
        return document;
    }

    public Customer toCustomerDomain(CustomerDocument d) {
        return new Customer(
                d.getId(),
                d.getCustomerType(),
                d.getDocumentType(),
                d.getDocumentNumber(),
                d.getProfileType() == null ? ProfileType.REGULAR : d.getProfileType(),
                d.getName(),
                d.getLastName(),
                d.getEmail(),
                d.getPhone(),
                d.isActive()
        );
    }
}
