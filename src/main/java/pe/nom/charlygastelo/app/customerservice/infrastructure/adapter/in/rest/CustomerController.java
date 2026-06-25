package pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.customerservice.application.usecase.CreateCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.DeleteCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.GetCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.ListCustomersUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.UpdateCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.domain.model.Customer;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.CreateCustomerRequest;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.CustomerResponse;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.dto.UpdateCustomerRequest;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.in.rest.mapper.RestMapper;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final RestMapper restMapper;

    @PostMapping
    public Single<ResponseEntity<CustomerResponse>> create(@RequestBody CreateCustomerRequest request) {
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
                                                           @RequestBody UpdateCustomerRequest request) {
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