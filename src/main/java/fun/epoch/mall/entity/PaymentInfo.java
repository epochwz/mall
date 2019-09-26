package fun.epoch.mall.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class PaymentInfo {
    private Integer id;
    private Integer userId;
    private Long orderNo;
    private Integer platform;
    private String platformNumber;
    private String platformStatus;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Date updateTime;
}