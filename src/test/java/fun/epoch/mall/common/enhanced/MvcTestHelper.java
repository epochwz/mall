package fun.epoch.mall.common.enhanced;

import com.fasterxml.jackson.core.type.TypeReference;
import fun.epoch.mall.common.helper.ServerResponseHelper;
import fun.epoch.mall.common.mock.Browser;
import fun.epoch.mall.common.mock.DataBase;
import fun.epoch.mall.common.mock.SQLLoader;
import fun.epoch.mall.utils.response.ResponseCode;
import fun.epoch.mall.utils.response.ServerResponse;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml", "classpath:spring-mvc.xml"})
public class MvcTestHelper {
    @Autowired
    private WebApplicationContext context;

    private Browser mvc;

    public MvcTestHelper init() {
        mvc = new Browser(context) {
            @Override
            public ResultActions expected(ResultActions resultActions, Object... expecteds) {
                if (expecteds != null && expecteds.length > 0 && expecteds[0] instanceof ResponseCode) {
                    expecteds[0] = ((ResponseCode) expecteds[0]).getCode();
                }
                return super.expected(resultActions, expecteds);
            }
        };
        return this;
    }

    public MvcTestHelper printable() {
        mvc.printable = true;
        return this;
    }

    protected MvcTestHelper session(String key, Object object) {
        mvc.session(key, object);
        return this;
    }

    protected MvcTestHelper session(MockHttpSession session) {
        mvc.session(session);
        return this;
    }

    public String content(ResultActions resultActions) {
        return mvc.response(resultActions);
    }

    public ResultActions expected(ResultActions resultActions, Object... expecteds) {
        return mvc.expected(resultActions, expecteds);
    }

    public void perform(ResponseCode responseCode, String... apis) {
        Arrays.stream(apis).forEach(api -> mvc.perform(post(api), responseCode));
    }

    public ResultActions postJson(String api, Object obj, Object... expecteds) {
        return mvc.postJson(api, obj, expecteds);
    }

    public ResultActions perform(MockHttpServletRequestBuilder request, Object... expecteds) {
        return mvc.perform(request, expecteds);
    }

    public ResultActions perform(ResponseCode responseCode, String api) {
        return perform(responseCode, post(api));
    }

    public ResultActions perform(ResponseCode responseCode, MockHttpServletRequestBuilder request) {
        return mvc.perform(request, responseCode);
    }

    /* ****************************** Helper ****************************** */
    private ServerResponseHelper<Object> helper = new ServerResponseHelper<>(new TypeReference<ServerResponse<Object>>() {
    });

    public <T> T dataFrom(ResultActions resultActions) {
        return (T) helper.dataOf(content(resultActions));
    }

    /* ****************************** Database ****************************** */
    @Autowired
    private DataSource dataSource;

    private static SQLLoader loader = new SQLLoader("# ");
    private static Map<String, Map<String, List<String>>> cache = new HashMap<>();

    public String[] resources;

    public DataBase database() {
        return database(resources);
    }

    public DataBase database(String... resources) {
        this.cache(resources);

        DataBase dataBase = new DataBase(dataSource);

        if (resources != null && resources.length > 0) {
            this.resources = resources;
            for (String resource : resources) {
                dataBase.load(cache.get(resource));
            }
        }

        return dataBase;
    }

    private void cache(String[] resources) {
        if (resources != null) {
            for (String resource : resources) {
                if (!cache.containsKey(resource)) {
                    cache.put(resource, loader.load(resource));
                }
            }
        }
    }
}
