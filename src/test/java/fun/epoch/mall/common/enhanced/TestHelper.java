package fun.epoch.mall.common.enhanced;


import fun.epoch.mall.utils.response.ResponseCode;
import fun.epoch.mall.utils.response.ServerResponse;

import java.util.Arrays;
import java.util.function.Function;

import static fun.epoch.mall.utils.response.ResponseCode.*;
import static org.junit.Assert.assertEquals;

/**
 * 辅助测试类：提供通用的测试辅助方法
 */
public class TestHelper {
    public static void assertObjectEquals(Object expected, Object actual) {
        assertEquals(expected.toString().trim(), actual.toString().trim());
    }

    public static void testIfCodeEqualsError(String[] errorValues, Function<String, ServerResponse> mapper) {
        Arrays.stream(errorValues).map(mapper).forEach(TestHelper::testIfCodeEqualsError);
    }

    public static ServerResponse testIfCodeEqualsError(ServerResponse response) {
        return testIfCodeEquals(ERROR, response);
    }

    public static ServerResponse testIfCodeEqualsForbidden(ServerResponse response) {
        return testIfCodeEquals(FORBIDDEN, response);
    }

    public static ServerResponse testIfCodeEqualsConflict(ServerResponse response) {
        return testIfCodeEquals(CONFLICT, response);
    }

    public static ServerResponse testIfCodeEqualsNotFound(ServerResponse response) {
        return testIfCodeEquals(NOT_FOUND, response);
    }

    public static <T> ServerResponse testIfCodeEqualsSuccess(ServerResponse<T> response) {
        return testIfCodeEquals(SUCCESS, response);
    }

    public static ServerResponse testIfCodeEquals(ResponseCode responseCode, ServerResponse response) {
        assertEquals(responseCode.getCode(), response.getCode());
        return response;
    }
}
