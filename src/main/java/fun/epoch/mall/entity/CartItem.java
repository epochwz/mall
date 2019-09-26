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
public class CartItem {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private Boolean checked;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Date updateTime;
}