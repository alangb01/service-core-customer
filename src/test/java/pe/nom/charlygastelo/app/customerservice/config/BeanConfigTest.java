package pe.nom.charlygastelo.app.customerservice.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pe.nom.charlygastelo.app.customerservice.application.usecase.CreateCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.GetCustomerUseCase;
import pe.nom.charlygastelo.app.customerservice.application.usecase.ListCustomersUseCase;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerCachePort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerEventProducerPort;
import pe.nom.charlygastelo.app.customerservice.domain.port.CustomerRepositoryPort;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.CustomerRepositoryAdapter;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.CustomerReactiveRepository;
import pe.nom.charlygastelo.app.customerservice.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import pe.nom.charlygastelo.app.customerservice.infrastructure.config.BeanConfig;

import static org.assertj.core.api.Assertions.assertThat;

class BeanConfigTest {

    private final BeanConfig beanConfig = new BeanConfig();

    @Test
    void customerRepositoryPortShouldReturnCustomerRepositoryAdapter() {
        CustomerReactiveRepository repository = Mockito.mock(CustomerReactiveRepository.class);
        PersistenceMapper mapper = Mockito.mock(PersistenceMapper.class);

        CustomerRepositoryPort result = beanConfig.customerRepositoryPort(repository, mapper);

        assertThat(result).isInstanceOf(CustomerRepositoryAdapter.class);
    }


}