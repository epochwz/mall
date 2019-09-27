package fun.epoch.mall.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class CartVo {
    private String imageHost;
    private BigDecimal cartTotalPrice;
    private boolean allChecked;
    private List<CartItemVo> cartItems;
}
