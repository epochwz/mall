package fun.epoch.mall.common.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

import static fun.epoch.mall.common.helper.FileHelper.load;

public class JsonHelper<T> {
    protected TypeReference<T> typeReference;

    public JsonHelper(TypeReference<T> typeReference) {
        this.typeReference = typeReference;
    }

    private ObjectMapper jackson = new ObjectMapper();

    public T of(String json) {
        try {
            return jackson.readValue(json, typeReference);
        } catch (IOException e) {
            String msg = String.format("parse json from JSON[%s] error: %s", json, e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }

    public T from(URL url) {
        try {
            return jackson.readValue(url, typeReference);
        } catch (IOException e) {
            String msg = String.format("parse json from URL[%s] error: %s", url, e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }

    public T from(String resource) {
        return from(load(resource));
    }
}
