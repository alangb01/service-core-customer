package pe.nom.charlygastelo.app.customerservice.domain.model;

import java.util.List;

public record ValidateResponse(
    boolean valid,
    String userId,
    String customerId,
    List<String>roles
) { }
