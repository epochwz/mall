package fun.epoch.mall.vo;

import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ProductVo {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private String mainImage;
    private String[] subImages;
    private String imageHost;
}
