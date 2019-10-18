package fun.epoch.mall.controller.portal;

import fun.epoch.mall.service.ShippingService;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

@RunWith(MockitoJUnitRunner.class)
public class ShippingControllerTest {
    @InjectMocks
    ShippingController controller;

    @Mock
    ShippingService service;

    @Mock
    MockHttpSession session;
}
