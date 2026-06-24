package pe.nom.charlygastelo.app.customerservice.domain.model;

public record Customer(
        String id,
        CustomerType customerType,
        DocumentType documentType,
        String documentNumber,
        String name,
        String lastName,
        String email,
        String phone,
        boolean active
) {
    public Customer updateWith(Customer newData) {
        return new Customer(
                this.id,                    // se mantiene
                newData.customerType,       // se actualiza
                newData.documentType,
                newData.documentNumber,
                newData.name,
                newData.lastName,
                newData.email,
                newData.phone,
                newData.active              // se mantiene
        );

    }
}