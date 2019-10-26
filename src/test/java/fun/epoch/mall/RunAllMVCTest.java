package fun.epoch.mall;

import fun.epoch.mall.mvc.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AuthorityInterceptorTest.class,
        ExceptionResolverTest.class,
        UserTest.class,
        CategoryTest.class,
        ProductTest.class,
        CartTest.class,
        ShippingTest.class,
        OrderTest.class
})
public class RunAllMVCTest {
}
