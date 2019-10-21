package fun.epoch.mall.controller.manage;

import com.github.pagehelper.PageInfo;
import fun.epoch.mall.service.OrderService;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/manage/order")
public class ManageOrderController {
    @Autowired
    OrderService orderService;

    @ResponseBody
    @RequestMapping(value = "detail.do")
    public ServerResponse<OrderVo> detail(@RequestParam long orderNo) {
        return orderService.detail(orderNo);
    }

    @ResponseBody
    @RequestMapping(value = "search.do")
    public ServerResponse<PageInfo<OrderVo>> search(
            Long orderNo,
            Integer userId,
            Integer status,
            Long startTime,
            Long endTime,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return orderService.search(orderNo, userId, null, status, startTime, endTime, pageNum, pageSize);
    }

    @ResponseBody
    @RequestMapping(value = "ship.do", method = POST)
    public ServerResponse ship(@RequestParam long orderNo) {
        return orderService.ship(orderNo);
    }

    @ResponseBody
    @RequestMapping(value = "close.do", method = POST)
    public ServerResponse close(@RequestParam long orderNo) {
        return orderService.close(orderNo);
    }
}
