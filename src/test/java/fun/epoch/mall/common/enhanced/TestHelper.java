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
    public static <T> void assertObjectEquals(Object expected, Object actual) {
        assertEquals(expected.toString().trim(), actual.toString().trim());
    }

    public static <T> void testIfCodeEqualsError(String[] errorValues, Function<String, ServerResponse<T>> mapper) {
        Arrays.stream(errorValues).map(mapper).forEach(TestHelper::testIfCodeEqualsError);
    }

    public static <T> ServerResponse<T> testIfCodeEqualsError(ServerResponse<T> response) {
        return testIfCodeEquals(ERROR, response);
    }

    public static <T> ServerResponse<T> testIfCodeEqualsForbidden(ServerResponse<T> response) {
        return testIfCodeEquals(FORBIDDEN, response);
    }

    public static <T> ServerResponse<T> testIfCodeEqualsConflict(ServerResponse<T> response) {
        return testIfCodeEquals(CONFLICT, response);
    }

    public static <T> ServerResponse<T> testIfCodeEqualsNotFound(ServerResponse<T> response) {
        return testIfCodeEquals(NOT_FOUND, response);
    }

    public static <T> ServerResponse<T> testIfCodeEqualsSuccess(ServerResponse<T> response) {
        return testIfCodeEquals(SUCCESS, response);
    }

    public static <T> ServerResponse<T> testIfCodeEquals(ResponseCode responseCode, ServerResponse<T> response) {
        assertEquals(responseCode.getCode(), response.getCode());
        return response;
    }
}
