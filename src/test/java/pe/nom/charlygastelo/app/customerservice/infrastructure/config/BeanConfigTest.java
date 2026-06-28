package pe.nom.charlygastelo.app.customerservice.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.nom.charlygastelo.app.customerservice.application.usecase.CreateCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.GetCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.ListCustomersUseCase;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventProducerPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.CustomerRepositoryAdapter;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.ReactiveCustomerRepository;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;

import static org.assertj.core.api.Assertions.assertThat;

class BeanConfigTest {

    private final BeanConfig beanConfig = new BeanConfig();

    @Test
    void customerRepositoryPortShouldReturnCustomerRepositoryAdapter() {
        ReactiveCustomerRepository repository = Mockito.mock(ReactiveCustomerRepository.class);
        PersistenceMapper mapper = Mockito.mock(PersistenceMapper.class);

        CustomerRepositoryPort result = beanConfig.customerRepositoryPort(repository, mapper);

        assertThat(result).isInstanceOf(CustomerRepositoryAdapter.class);
    }

    @Test
    void createCustomerUseCaseShouldReturnCreateCustomerUseCase() {
        CustomerRepositoryPort repository = Mockito.mock(CustomerRepositoryPort.class);
        CustomerEventProducerPort producerPort = Mockito.mock(CustomerEventProducerPort.class);
        CreateCustomerUseCase result = beanConfig.createCustomerUseCase(repository, producerPort);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CreateCustomerUseCase.class);
    }

    @Test
    void getCustomerUseCaseShouldReturnGetCustomerUseCase() {
        CustomerRepositoryPort repository = Mockito.mock(CustomerRepositoryPort.class);
        CustomerCachePort cache = Mockito.mock(CustomerCachePort.class);
        GetCustomerUseCase result = beanConfig.getCustomerUseCase(repository,cache);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(GetCustomerUseCase.class);
    }

    @Test
    void listCustomersUseCaseShouldReturnListCustomersUseCase() {
        CustomerRepositoryPort repository = Mockito.mock(CustomerRepositoryPort.class);

        ListCustomersUseCase result = beanConfig.listCustomersUseCase(repository);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(ListCustomersUseCase.class);
    }
}