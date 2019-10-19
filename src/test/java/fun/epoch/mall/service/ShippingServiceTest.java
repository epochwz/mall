package fun.epoch.mall.service;

import fun.epoch.mall.dao.ShippingMapper;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ShippingServiceTest {
    @InjectMocks
    ShippingService service;

    @Mock
    ShippingMapper mapper;
}
