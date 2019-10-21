package fun.epoch.mall.controller.portal;

import fun.epoch.mall.service.OrderService;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
    @InjectMocks
    OrderController controller;

    @Mock
    OrderService service;
}
