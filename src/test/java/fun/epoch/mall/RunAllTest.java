package fun.epoch.mall;

import fun.epoch.mall.controller.manage.ManageCategoryControllerTest;
import fun.epoch.mall.controller.manage.ManageOrderControllerTest;
import fun.epoch.mall.controller.manage.ManageProductControllerTest;
import fun.epoch.mall.controller.manage.ManageUserControllerTest;
import fun.epoch.mall.controller.portal.OrderControllerTest;
import fun.epoch.mall.controller.portal.ProductControllerTest;
import fun.epoch.mall.controller.portal.ShippingControllerTest;
import fun.epoch.mall.controller.portal.UserControllerTest;
import fun.epoch.mall.service.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserControllerTest.class,
        ManageUserControllerTest.class,
        UserServiceTest.class,
        ManageCategoryControllerTest.class,
        CategoryServiceTest.class,
        ProductControllerTest.class,
        ManageProductControllerTest.class,
        ProductServiceTest.class,
        ShippingControllerTest.class,
        ShippingServiceTest.class,
        ManageOrderControllerTest.class,
        OrderControllerTest.class,
        OrderServiceTest.class
})
public class RunAllTest {
}
