package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto;

public record CustomerResponse(
        String id,
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