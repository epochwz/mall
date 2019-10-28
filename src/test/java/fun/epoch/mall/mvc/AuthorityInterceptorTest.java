package fun.epoch.mall.mvc;

import fun.epoch.mall.mvc.common.Apis;
import fun.epoch.mall.mvc.common.CustomMvcTest;
import org.junit.Before;
import org.junit.Test;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.utils.response.ResponseCode.FORBIDDEN;
import static fun.epoch.mall.utils.response.ResponseCode.NEED_LOGIN;

public class AuthorityInterceptorTest extends CustomMvcTest {
    @Before
    public void setup() {
        this.init();
    }

    @Test
    public void testReturnNeedLogin_whenAccessApiWithoutLogin() {
        this.perform(NEED_LOGIN, managerUrls);
        this.perform(NEED_LOGIN, consumerUrls);
    }

    @Test
    public void testReturnForbidden_whenAccessApiWithoutAuthority() {
        this.session(CONSUMER).perform(FORBIDDEN, managerUrls);
        this.session(MANAGER).perform(FORBIDDEN, consumerUrls);
    }

    private static final String[] consumerUrls = {
            Apis.portal.user.info

            , Apis.portal.user.update
            , Apis.portal.user.reset_password

            , Apis.portal.shipping.add
            , Apis.portal.shipping.delete
            , Apis.portal.shipping.update
            , Apis.portal.shipping.detail
            , Apis.portal.shipping.list

            , Apis.portal.cart.list
            , Apis.portal.cart.count
            , Apis.portal.cart.add
            , Apis.portal.cart.delete
            , Apis.portal.cart.update
            , Apis.portal.cart.check
            , Apis.portal.cart.check_all

            , Apis.portal.order.search
            , Apis.portal.order.detail
            , Apis.portal.order.preview
            , Apis.portal.order.create
            , Apis.portal.order.cancel

            , Apis.portal.order.pay
            , Apis.portal.order.payment_status

    };

    private static final String[] managerUrls = {
            Apis.manage.product.add
            , Apis.manage.product.update
            , Apis.manage.product.shelve
            , Apis.manage.product.search
            , Apis.manage.product.detail
            , Apis.manage.product.upload
            , Apis.manage.product.upload_by_simditor

            , Apis.manage.order.search
            , Apis.manage.order.detail
            , Apis.manage.order.ship
            , Apis.manage.order.close

            , Apis.manage.category.add
            , Apis.manage.category.update
            , Apis.manage.category.enable
            , Apis.manage.category.disable
            , Apis.manage.category.list
            , Apis.manage.category.list_all
    };
}
