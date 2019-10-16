package fun.epoch.mall.service;

import fun.epoch.mall.dao.CartItemMapper;
import fun.epoch.mall.dao.ProductMapper;
import fun.epoch.mall.entity.CartItem;
import fun.epoch.mall.entity.Product;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.CartItemVo;
import fun.epoch.mall.vo.CartVo;
import fun.epoch.mall.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fun.epoch.mall.common.Constant.SaleStatus.ON_SALE;
import static fun.epoch.mall.common.Constant.SettingKeys.IMAGE_HOST;
import static fun.epoch.mall.common.Constant.settings;

@Service
public class CartService {
    @Autowired
    CartItemMapper cartItemMapper;

    @Autowired
    ProductMapper productMapper;

    @Transactional
    public ServerResponse<CartVo> list(int userId) {
        List<CartItem> cartItems = cartItemMapper.selectByUserId(userId);
        return ServerResponse.success(toCartVo(cartItems));
    }

    public ServerResponse<Integer> count(int userId) {
        int count = cartItemMapper.selectCountByUserId(userId);
        return ServerResponse.success(count);
    }

    public ServerResponse<CartVo> add(int userId, int productId, int count) {
        if (count > 0) {
            CartItem item = cartItemMapper.selectByUserIdAndProductId(userId, productId);
            if (item != null) {
                item.setQuantity(item.getQuantity() + count);
                cartItemMapper.updateSelectiveByPrimaryKey(item);
            } else {
                CartItem cartItem = CartItem.builder()
                        .userId(userId)
                        .productId(productId)
                        .quantity(count)
                        .checked(true)
                        .build();
                cartItemMapper.insert(cartItem);
            }
        }
        return list(userId);
    }

    public ServerResponse<CartVo> delete(int userId, int[] productIds) {
        if (productIds != null && productIds.length > 0) {
            List<Integer> ids = Arrays.stream(productIds).boxed().collect(Collectors.toList());
            cartItemMapper.deleteByUserIdAndProductIds(userId, ids);
        }
        return list(userId);
    }

    public ServerResponse<CartVo> update(int userId, int productId, int count) {
        if (count > 0) {
            cartItemMapper.updateQuantityByUserIdAndProductId(userId, productId, count);
        }
        return list(userId);
    }

    public ServerResponse<CartVo> check(int userId, int productId, boolean checked) {
        cartItemMapper.updateCheckStatusByUserIdAndProductId(userId, productId, checked);
        return list(userId);
    }

    public ServerResponse<CartVo> checkAll(int userId, boolean checked) {
        cartItemMapper.updateCheckStatusByUserId(userId, checked);
        return list(userId);
    }

    private CartVo toCartVo(List<CartItem> items) {
        List<CartItemVo> vos = new ArrayList<>();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        boolean allChecked = true;

        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                Product product = productMapper.selectByPrimaryKey(item.getProductId());
                if (product != null && product.getStatus() == ON_SALE) {
                    CartItemVo vo = toCartItemVo(item, product);

                    vos.add(vo);
                    if (allChecked && !vo.isChecked()) allChecked = false;
                    if (vo.isChecked()) cartTotalPrice = cartTotalPrice.add(vo.getTotalPrice());
                } else {
                    cartItemMapper.deleteByPrimaryKey(item.getId());
                }
            }
        }

        return CartVo.builder().imageHost(settings.get(IMAGE_HOST)).allChecked(allChecked).cartTotalPrice(cartTotalPrice).cartItems(vos).build();
    }

    private CartItemVo toCartItemVo(CartItem item, Product product) {
        boolean isExceedLimit = redressQuantityLimit(item, product);
        String productImage = ProductVo.extractMainImage(product);
        BigDecimal totalPrice = calculateTotalPrice(product.getPrice(), item.getQuantity());
        return CartItemVo.builder()
                .productId(item.getProductId())
                .productName(product.getName())
                .productImage(productImage)
                .unitPrice(product.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(totalPrice)
                .checked(item.getChecked())
                .limit(isExceedLimit)
                .build();
    }

    /**
     * 检查购物车商品数量是否超出限制，并纠正购物车商品数量
     *
     * @param item    购物车商品条目
     * @param product 购物车商品信息
     * @return 购物车商品数量是否超出限制
     */
    private boolean redressQuantityLimit(CartItem item, Product product) {
        boolean limit = item.getQuantity() > product.getStock();
        if (limit) {
            item.setQuantity(product.getStock());
            cartItemMapper.updateByPrimaryKey(item);
        }
        return limit;
    }

    private BigDecimal calculateTotalPrice(BigDecimal unitPrice, int quantity) {
        if (unitPrice != null) {
            return unitPrice.multiply(new BigDecimal(quantity));
        }
        return new BigDecimal("0");
    }
}
