package fun.epoch.mall.mvc.common;

/**
 * 系统接口
 */
public interface Apis {
    interface portal {
        interface user {
            String register = "/user/register.do";
            String account_verify = "/user/account_verify.do";
            String login = "/user/login.do";
            String logout = "/user/logout.do";
            String info = "/user/info.do";
            String update = "/user/update.do";
            String reset_password = "/user/reset_password.do";
            String forget_password = "/user/forget_password.do";
            String commit_answer = "/user/commit_answer.do";
            String reset_password_by_token = "/user/reset_password_by_token.do";
        }

        interface shipping {
            String detail = "/shipping/detail.do";
            String list = "/shipping/list.do";
            String add = "/shipping/add.do";
            String update = "/shipping/update.do";
            String delete = "/shipping/delete.do";
        }

        interface product {
            String detail = "/product/detail.do";
            String search = "/product/search.do";
        }

        interface cart {
            String list = "/cart/list.do";
            String count = "/cart/count.do";
            String add = "/cart/add.do";
            String update = "/cart/update.do";
            String delete = "/cart/delete.do";
            String check = "/cart/check.do";
            String check_all = "/cart/check_all.do";
        }

        interface order {
            String detail = "/order/detail.do";
            String search = "/order/search.do";
            String preview = "/order/preview.do";
            String create = "/order/create.do";
            String cancel = "/order/cancel.do";
            String pay = "/order/pay.do";
            String payment_status = "/order/payment_status.do";
        }

        interface payment {
            String callback = "/payment/alipay/callback.do";
        }
    }

    interface manage {
        interface user {
            String login = "/manage/user/login.do";
            String logout = "/manage/user/logout.do";
        }

        interface product {
            String detail = "/manage/product/detail.do";
            String search = "/manage/product/search.do";
            String add = "/manage/product/add.do";
            String update = "/manage/product/update.do";
            String shelve = "/manage/product/shelve.do";
            String upload = "/manage/product/upload.do";
            String upload_by_simditor = "/manage/product/upload_by_simditor.do";
        }

        interface order {
            String detail = "/manage/order/detail.do";
            String search = "/manage/order/search.do";
            String ship = "/manage/order/ship.do";
            String close = "/manage/order/close.do";
        }

        interface category {
            String list = "/manage/category/list.do";
            String list_all = "/manage/category/list_all.do";
            String add = "/manage/category/add.do";
            String update = "/manage/category/update.do";
            String enable = "/manage/category/enable.do";
            String disable = "/manage/category/disable.do";
        }
    }
}