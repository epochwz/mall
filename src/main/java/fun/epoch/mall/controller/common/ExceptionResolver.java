package fun.epoch.mall.controller.common;

import fun.epoch.mall.utils.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        log.error("Internal Server Error on Request: [{}]", httpServletRequest.getRequestURI(), e);

        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());

        modelAndView.addObject("code", ResponseCode.INTERNAL_SERVER_ERROR.getCode());
        modelAndView.addObject("msg", ResponseCode.INTERNAL_SERVER_ERROR.getMsg());
        modelAndView.addObject("data", e.getMessage());

        return modelAndView;
    }
}
