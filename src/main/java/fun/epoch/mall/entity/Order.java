package fun.epoch.mall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Order {
    private Integer id;
    private Long orderNo;
    private Integer userId;
    private Integer shippingId;
    private Integer status;
    private BigDecimal postage;
    private BigDecimal payment;
    private Integer paymentType;
    private Date paymentTime;
    private Date sendTime;
    private Date endTime;
    private Date closeTime;
    private Date createTime;
    @JsonIgnore
    private Date updateTime;
}