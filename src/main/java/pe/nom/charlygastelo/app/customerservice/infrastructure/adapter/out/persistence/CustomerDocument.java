package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document("customers")
public class CustomerDocument {

    @Id
    private String id;
    private String customerType;
    private String documentType;
    private String documentNumber;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private boolean active;
}