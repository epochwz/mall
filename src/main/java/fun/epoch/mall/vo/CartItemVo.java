package fun.epoch.mall.vo;

import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class CartItemVo {
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;
    private boolean checked;
    private boolean limit;
}
