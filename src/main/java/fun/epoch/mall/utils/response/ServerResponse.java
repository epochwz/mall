package fun.epoch.mall.utils.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import static fun.epoch.mall.utils.response.ResponseCode.ERROR;
import static fun.epoch.mall.utils.response.ResponseCode.SUCCESS;

/**
 * 服务端通用响应对象
 */
public class ServerResponse<T> {
    private int code;
    private String msg;
    @JsonInclude()
    private T data;

    // 请求是否成功
    @JsonIgnore
    public boolean isSuccess() {
        return code == SUCCESS.getCode();
    }

    // 请求是否失败
    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

    /* 通用请求成功响应 */
    public static <T> ServerResponse<T> success() {
        return new ServerResponse<>(SUCCESS.getCode(), SUCCESS.getMsg());
    }

    public static <T> ServerResponse<T> success(T data) {
        return new ServerResponse<>(SUCCESS.getCode(), SUCCESS.getMsg(), data);
    }

    public static <T> ServerResponse<T> success(T data, String msg) {
        return new ServerResponse<>(SUCCESS.getCode(), msg, data);
    }

    /* 通用请求失败响应 */
    public static <T> ServerResponse<T> error() {
        return error(ERROR);
    }

    public static <T> ServerResponse<T> error(String msg) {
        return error(ERROR, msg);
    }

    public static <T> ServerResponse<T> error(String msg, T data) {
        return error(ERROR, msg, data);
    }

    /* 特定请求失败响应 */
    public static <T> ServerResponse<T> error(ResponseCode responseCode) {
        return new ServerResponse<>(responseCode.getCode(), responseCode.getMsg());
    }

    public static <T> ServerResponse<T> error(ResponseCode responseCode, String msg) {
        return new ServerResponse<>(responseCode.getCode(), msg);
    }

    public static <T> ServerResponse<T> error(ResponseCode responseCode, String msg, T data) {
        return new ServerResponse<>(responseCode.getCode(), msg, data);
    }

    /* 自定义请求响应 */
    public static <T> ServerResponse<T> response(int code, String msg, T data) {
        return new ServerResponse<>(code, msg, data);
    }

    public static <T> ServerResponse<T> response(int code, String msg) {
        return new ServerResponse<>(code, msg);
    }

    public static <T> ServerResponse<T> response(ServerResponse response) {
        return ServerResponse.response(response.getCode(), response.getMsg());
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public ServerResponse() {
    }

    private ServerResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private ServerResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
