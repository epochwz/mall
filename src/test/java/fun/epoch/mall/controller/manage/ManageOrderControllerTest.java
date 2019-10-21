package fun.epoch.mall.controller.manage;

import fun.epoch.mall.service.OrderService;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ManageOrderControllerTest {
    @InjectMocks
    ManageOrderController controller;

    @Mock
    OrderService service;
}
