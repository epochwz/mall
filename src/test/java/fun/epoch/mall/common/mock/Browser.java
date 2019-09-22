package fun.epoch.mall.common.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 模拟浏览器
 */
public class Browser {
    private MockMvc mvc;
    private MockHttpSession session;

    public Browser(MockMvc mvc) {
        this.mvc = mvc;
        this.session = new MockHttpSession();
    }

    public Browser(WebApplicationContext context) {
        this(MockMvcBuilders.webAppContextSetup(context).build());
    }

    public Browser session(String key, Object object) {
        this.session.setAttribute(key, object);
        return this;
    }

    public Browser session(MockHttpSession session) {
        this.session = session;
        return this;
    }

    public ResultActions postJson(String api, Object obj, Object... expecteds) {
        MockHttpServletRequestBuilder postJson = post(api)
                .contentType("application/json")
                .content(toJson(obj));
        return perform(postJson, expecteds);
    }

    public boolean printable = false;

    public ResultActions perform(MockHttpServletRequestBuilder request, Object... expecteds) {
        try {
            ResultActions resultActions = this.mvc.perform(request.session(session));
            if (printable) println(resultActions);
            return expected(resultActions.andExpect(status().isOk()), expecteds);
        } catch (Exception e) {
            throw new RuntimeException("perform error: " + e.getMessage(), e);
        }
    }

    public String response(ResultActions resultActions) {
        try {
            return resultActions.andReturn().getResponse().getContentAsString();
        } catch (UnsupportedEncodingException e) {
            String msg = String.format("parse json from ResultActions[%s] error: %s", resultActions, e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }

    public ResultActions println(ResultActions resultActions) {
        try {
            return resultActions.andDo(print());
        } catch (Exception e) {
            throw new RuntimeException("print error: " + e.getMessage(), e);
        }
    }

    public ResultActions expected(ResultActions resultActions, Object... expecteds) {
        try {
            if (expecteds != null) {
                for (Object expected : expecteds) {
                    resultActions.andExpect(content().string(containsString(String.valueOf(expected))));
                }
            }
            return resultActions;
        } catch (Exception e) {
            throw new RuntimeException("expected error: " + e.getMessage(), e);
        }
    }

    private static ObjectMapper jackson = new ObjectMapper();

    private static String toJson(Object obj) {
        try {
            return jackson.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("toJson error: " + e.getMessage(), e);
        }
    }
}
