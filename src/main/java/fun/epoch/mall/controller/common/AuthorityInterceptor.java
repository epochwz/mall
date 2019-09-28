package fun.epoch.mall.controller.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.epoch.mall.entity.User;
import fun.epoch.mall.utils.response.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static fun.epoch.mall.common.Constant.AccountRole.CONSUMER;
import static fun.epoch.mall.common.Constant.AccountRole.MANAGER;
import static fun.epoch.mall.common.Constant.CURRENT_USER;
import static fun.epoch.mall.utils.response.ResponseCode.FORBIDDEN;
import static fun.epoch.mall.utils.response.ResponseCode.NEED_LOGIN;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    private ObjectMapper jackson = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String uri = httpServletRequest.getRequestURI();
        log.debug("全局权限拦截器已拦截请求 [{}]", uri);

        User currentUser = (User) httpServletRequest.getSession().getAttribute(CURRENT_USER);
        if (currentUser == null) {
            responseAsJson(httpServletResponse, ServerResponse.error(NEED_LOGIN, "未登录"));
            log.info("请求 [{}] 已被拒绝：未登录", uri);
            return false;
        }

        if ((uri.contains("manage") && MANAGER != currentUser.getRole())
                || (!uri.contains("manage") && CONSUMER != currentUser.getRole())
        ) {
            responseAsJson(httpServletResponse, ServerResponse.error(FORBIDDEN, "账号访问权限不足"));
            log.info("请求 [{}] 已被拒绝：账号 [{}] 访问权限不足", uri, currentUser.getUsername());
            return false;
        }

        log.debug("全局权限拦截器已通过请求 [{}]", uri);
        return true;
    }

    private void responseAsJson(HttpServletResponse httpServletResponse, ServerResponse serverResponse) throws IOException {
        String json = jackson.writeValueAsString(serverResponse);
        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.getWriter().write(json);
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {

    }
}
