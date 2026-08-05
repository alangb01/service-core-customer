package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.customerservice.application.usecase.*;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.CustomerCreateRequest;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.CustomerResponse;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.CustomerUpdateRequest;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.mapper.RestMapper;

@RestController
@RequestMapping("/api/customers")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final RestMapper restMapper;

    @PostMapping
    public Single<ResponseEntity<CustomerResponse>> create(@RequestBody CustomerCreateRequest request) {
        Customer customer = restMapper.toDomain(request);

        return createCustomerUseCase.execute(customer)
                .map(saved -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(restMapper.toResponse(saved)));
    }

    @GetMapping("/{id}")
    public Single<ResponseEntity<CustomerResponse>> getById(@PathVariable String id) {
        return getCustomerUseCase.byId(id)
                .map(customer -> ResponseEntity.ok(restMapper.toResponse(customer)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/document")
    public Single<ResponseEntity<CustomerResponse>> getByDocument(@RequestParam String type,
                                                                  @RequestParam String number) {
        return getCustomerUseCase.byDocument(type, number)
                .map(customer -> ResponseEntity.ok(restMapper.toResponse(customer)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flowable<CustomerResponse> list() {
        return listCustomersUseCase.all()
                .map(restMapper::toResponse);
    }

    @PutMapping("/{id}")
    public Single<ResponseEntity<CustomerResponse>> update(@PathVariable String id,
                                                           @RequestBody CustomerUpdateRequest request) {
        Customer customer = restMapper.toDomain(request);

        return updateCustomerUseCase.execute(id, customer)
                .map(updated -> ResponseEntity.ok(restMapper.toResponse(updated)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public @NonNull Single<ResponseEntity<Object>> delete(@PathVariable String id) {

        return getCustomerUseCase.byId(id)
                .flatMapSingle(customer ->
                        deleteCustomerUseCase.execute(id)
                                .toSingleDefault(ResponseEntity.<Void>noContent().build())
                ).switchIfEmpty(Single.just(ResponseEntity.notFound().build()));
    }
}