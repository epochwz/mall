package fun.epoch.mall.controller.portal;

import fun.epoch.mall.entity.User;
import fun.epoch.mall.service.ShippingService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.mvc.common.Keys.UserKeys.UserId;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShippingControllerTest {
    @InjectMocks
    ShippingController controller;

    @Mock
    ShippingService service;

    @Mock
    MockHttpSession session;

    @Before
    public void setup() {
        when(session.getAttribute(CURRENT_USER)).thenReturn(User.builder().id(UserId).build());
    }
}
