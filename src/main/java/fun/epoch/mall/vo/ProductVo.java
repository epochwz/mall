package fun.epoch.mall.vo;

import fun.epoch.mall.entity.Product;
import lombok.*;

import java.math.BigDecimal;

import static fun.epoch.mall.utils.TextUtils.isBlank;

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

    public Product to() {
        return Product.builder()
                .id(id)
                .categoryId(categoryId)
                .name(name)
                .subtitle(subtitle)
                .detail(detail)
                .price(price)
                .stock(stock)
                .status(status)
                .mainImage(extractMainImage(mainImage, subImages))
                .subImages(array2String(subImages))
                .build();
    }

    public String array2String(String[] subImages) {
        StringBuilder result = new StringBuilder();
        if (subImages != null && subImages.length > 0) {
            for (String subImage : subImages) {
                result.append(subImage).append(",");
            }
            result.deleteCharAt(result.lastIndexOf(","));
        }
        return result.toString();
    }

    public String extractMainImage(String mainImage, String[] subImages) {
        if (isBlank(mainImage)) {
            if (subImages != null && subImages.length > 0) {
                return subImages[0];
            }
        }
        return mainImage;
    }
}
