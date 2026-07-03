package pe.nom.charlygastelo.app.customerservice.security;

import java.util.List;

public record UserPrincipal(
        String userId,
        String customerId,
        List<String> roles
) {}
