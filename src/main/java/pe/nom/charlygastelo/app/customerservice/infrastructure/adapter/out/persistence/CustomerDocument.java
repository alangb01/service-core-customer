package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import pe.nom.charlygastelo.app.customerservice.domain.model.CustomerType;
import pe.nom.charlygastelo.app.customerservice.domain.model.DocumentType;
import pe.nom.charlygastelo.app.customerservice.domain.model.ProfileType;

@Data
@Document("customers")
public class CustomerDocument {

    @Id
    private String id;
    private CustomerType customerType;
    private DocumentType documentType;
    private String documentNumber;
    private ProfileType profileType;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private boolean active;
}