package fun.epoch.mall.vo;

import fun.epoch.mall.entity.Product;
import lombok.*;

import java.math.BigDecimal;

import static fun.epoch.mall.common.Constant.SettingKeys.IMAGE_HOST;
import static fun.epoch.mall.common.Constant.settings;
import static fun.epoch.mall.utils.TextUtils.isBlank;
import static fun.epoch.mall.utils.TextUtils.isNotBlank;

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

    public ProductVo(Product product) {
        setId(product.getId());
        setCategoryId(product.getCategoryId());
        setName(product.getName());
        setSubtitle(product.getSubtitle());
        setDetail(product.getDetail());
        setPrice(product.getPrice());
        setStock(product.getStock());
        setStatus(product.getStatus());
        setMainImage(extractMainImage(product));
        setSubImages(str2Array(product.getSubImages()));
        setImageHost(settings.get(IMAGE_HOST));
    }

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
                .subImages(array2Str(subImages))
                .build();
    }

    public static String[] str2Array(String images) {
        return isNotBlank(images) ? images.split(",") : new String[0];
    }

    public static String array2Str(String[] subImages) {
        StringBuilder result = new StringBuilder();
        if (subImages != null && subImages.length > 0) {
            for (String subImage : subImages) {
                result.append(subImage).append(",");
            }
            result.deleteCharAt(result.lastIndexOf(","));
        }
        return result.toString();
    }

    public static String extractMainImage(Product product) {
        String mainImage = product.getMainImage();
        String[] subImages = str2Array(product.getSubImages());
        return extractMainImage(mainImage, subImages);
    }

    public static String extractMainImage(String mainImage, String[] subImages) {
        if (isBlank(mainImage)) {
            if (subImages != null && subImages.length > 0) {
                return subImages[0];
            }
        }
        return mainImage;
    }
}
