package fun.epoch.mall.controller.manage;

import fun.epoch.mall.service.UserService;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

@RunWith(MockitoJUnitRunner.class)
public class ManageUserControllerTest {
    @InjectMocks
    private ManageUserController controller;

    @Mock
    private UserService service;

    @Mock
    private MockHttpSession session;

}
