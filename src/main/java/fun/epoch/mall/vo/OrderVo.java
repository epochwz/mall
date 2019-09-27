package fun.epoch.mall.vo;

import fun.epoch.mall.entity.OrderItem;
import fun.epoch.mall.entity.Shipping;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class OrderVo {
    private Integer userId;
    private Long orderNo;
    private BigDecimal payment;
    private BigDecimal postage;
    private Integer status;
    private String statusDesc;
    private String createTime;
    private Integer paymentType;
    private String paymentTypeDesc;
    private String paymentTime;
    private String shipTime;
    private String endTime;
    private String closeTime;
    private Shipping shipping;
    private List<OrderItem> products;
}
