package pe.nom.charlygastelo.app.customerservice.adapter.in.rest.dto;


public record CustomerCreateRequest(
        String customerType,
        String documentType,
        String documentNumber,
        String name,
        String lastName,
        String email,
        String phone,
        boolean active
) {
}