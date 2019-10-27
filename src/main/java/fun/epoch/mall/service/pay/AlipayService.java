package fun.epoch.mall.service.pay;

import com.alipay.api.AlipayResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import fun.epoch.mall.common.Constant;
import fun.epoch.mall.dao.OrderMapper;
import fun.epoch.mall.dao.PaymentInfoMapper;
import fun.epoch.mall.entity.Order;
import fun.epoch.mall.entity.OrderItem;
import fun.epoch.mall.entity.PaymentInfo;
import fun.epoch.mall.service.FTPService;
import fun.epoch.mall.service.PaymentService;
import fun.epoch.mall.utils.DateTimeUtils;
import fun.epoch.mall.utils.TextUtils;
import fun.epoch.mall.utils.response.ServerResponse;
import fun.epoch.mall.vo.OrderVo;
import fun.epoch.mall.vo.QrCodeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fun.epoch.mall.common.Constant.OrderStatus.PAID;
import static fun.epoch.mall.common.Constant.PaymentPlatform.ALIPAY;
import static fun.epoch.mall.utils.response.ResponseCode.INTERNAL_SERVER_ERROR;

@Slf4j
@Service
public class AlipayService implements PaymentService {
    static {
        Configs.init("alipay.properties");
    }

    public static final String ALIPAY_CALLBACK_URL = Constant.settings.get(Constant.SettingKeys.ALIPAY_CALLBACK_URL);
    public static final String UPLOAD_REMOTE_PATH = "order";
    private static AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

    @Autowired
    FTPService ftp;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Override
    public ServerResponse<QrCodeVo> preOrder(OrderVo order) {
        ServerResponse<String> sendPreOrderRequest = sendPreOrderRequest(toPreOrderRequest(order));
        if (sendPreOrderRequest.isError()) {
            return ServerResponse.response(sendPreOrderRequest);
        }

        String qrCode = sendPreOrderRequest.getData();
        return uploadQrCode(qrCode, order.getOrderNo());
    }

    @Transactional
    @Override
    public ServerResponse<Object> callback(Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return ServerResponse.error("支付宝回调：回调参数异常");
        }

        long orderNo = Long.parseLong(params.get("out_trade_no"));

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.error(String.format("支付宝回调：找不到订单 [%s]", orderNo));
        }

        if (order.getStatus() >= PAID.getCode()) {
            return ServerResponse.success("支付宝回调：重复回调");
        }

        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        if (Constant.AlipayCallbackCode.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            order.setPaymentTime(DateTimeUtils.from(params.get("gmt_payment")));
            order.setStatus(PAID.getCode());
            if (orderMapper.updateSelectiveByPrimaryKey(order) != 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ServerResponse.error(String.format("支付宝回调：更新订单 [%s] 状态失败", orderNo));
            }
        }

        PaymentInfo payInfo = new PaymentInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPlatform(ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        if (paymentInfoMapper.insert(payInfo) != 1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ServerResponse.error("支付宝回调：生成支付信息表失败");
        }
        return ServerResponse.success();
    }

    /* ****************************** 上传支付二维码 ****************************** */
    private ServerResponse<QrCodeVo> uploadQrCode(String qrCodeData, Long orderNo) {
        File qrCodeImage = null;
        try {
            qrCodeImage = File.createTempFile("qrcode-", ".jpg");
            ZxingUtils.getQRCodeImge(qrCodeData, 256, qrCodeImage.getPath());

            String qrCodeUrl = ftp.uploadWithUrl(UPLOAD_REMOTE_PATH, qrCodeImage);
            if (qrCodeUrl == null) {
                QrCodeVo errorData = QrCodeVo.builder().orderNo(orderNo).qrCode(qrCodeData).build();
                String errorMsg = String.format("订单 [%s] 预下单失败，支付二维码上传失败！二维码原始数据存储在响应参数 [qrCode] 中", orderNo);
                return ServerResponse.error(INTERNAL_SERVER_ERROR, errorMsg, errorData);
            }
            return ServerResponse.success(QrCodeVo.builder().orderNo(orderNo).qrCode(qrCodeUrl).build());
        } catch (Exception e) {
            return ServerResponse.error(INTERNAL_SERVER_ERROR, String.format("订单 [%s] 预下单失败，支付二维码上传失败：创建临时文件失败！", orderNo));
        } finally {
            if (qrCodeImage != null) {
                qrCodeImage.delete();
            }
        }
    }

    /* ****************************** 发送预下单请求 ****************************** */
    private ServerResponse<String> sendPreOrderRequest(AlipayTradePrecreateRequestBuilder preRequest) {
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(preRequest);
        dumpResponse(result.getResponse());
        String orderNo = preRequest.getOutTradeNo();
        switch (result.getTradeStatus()) {
            case SUCCESS:
                String msg = String.format("支付宝预下单成功: 订单号[%s]", orderNo);
                String qrCode = result.getResponse().getQrCode();
                return successResponse(qrCode, msg);
            case FAILED:
                return errorResponse(String.format("支付宝预下单失败: 订单号[%s]", orderNo));
            case UNKNOWN:
                return errorResponse(String.format("支付宝预下单失败: 订单号[%s]，错误原因：%s", orderNo, "支付宝系统异常，预下单状态未知"));
            default:
                return errorResponse(String.format("支付宝预下单失败: 订单号[%s]，错误原因：%s", orderNo, "不支持的交易状态，交易返回异常"));
        }
    }

    private ServerResponse<String> successResponse(String data, String msg) {
        log.info(msg);
        return ServerResponse.success(data, msg);
    }

    private ServerResponse<String> errorResponse(String errorMsg) {
        log.error(errorMsg);
        return ServerResponse.error(errorMsg);
    }

    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info("code:{}, msg:{}", response.getCode(), response.getMsg());
            if (TextUtils.isNotBlank(response.getSubCode())) {
                log.info("subCode:{}, subMsg:{}", response.getSubCode(), response.getSubMsg());
            }
            log.info("body:" + response.getBody());
        }
    }

    /* ****************************** 构造预下单请求 ****************************** */
    private AlipayTradePrecreateRequestBuilder toPreOrderRequest(OrderVo order) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "Mall 商城订单扫码支付";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = String.format("购买商品 %s 件, 共 %s 元", order.getProducts().size(), order.getPayment());

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "S9527";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "S007";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = goods(order);

        // 创建扫码支付请求builder，设置请求参数
        return new AlipayTradePrecreateRequestBuilder()
                .setOutTradeNo(outTradeNo).setTotalAmount(totalAmount)
                .setGoodsDetailList(goodsDetailList)
                .setSubject(subject).setBody(body)
                .setSellerId(sellerId).setStoreId(storeId).setOperatorId(operatorId)
                .setUndiscountableAmount(undiscountableAmount)
                .setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(ALIPAY_CALLBACK_URL); // 支付宝回调接口
    }

    private List<GoodsDetail> goods(OrderVo order) {
        return order.getProducts().stream().map(this::good).collect(Collectors.toList());
    }

    private GoodsDetail good(OrderItem product) {
        return GoodsDetail.newInstance(product.getProductId().toString(), product.getProductName(), product.getUnitPrice().longValue(), product.getQuantity());
    }
}
