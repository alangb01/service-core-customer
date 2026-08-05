package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.client.dto;

import java.util.List;

public record ValidateResponse(
    boolean valid,
    String userId,
    String customerId,
    List<String>roles
) { }
