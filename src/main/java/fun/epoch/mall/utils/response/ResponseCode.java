package fun.epoch.mall.utils.response;

/**
 * 服务端通用响应码
 */
public enum ResponseCode {
    SUCCESS(200, "OK"),
    ERROR(400, "Bad Request"),
    UN_AUTHORIZED(401, "UnAuthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    NEED_LOGIN(999, "Need Login"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error. Please check the system log"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    ;

    private int code;
    private String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
