package fun.epoch.mall.common.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import fun.epoch.mall.utils.response.ServerResponse;

import java.net.URL;

public class ServerResponseHelper<T> extends JsonHelper<ServerResponse<T>> {
    public ServerResponseHelper(TypeReference<ServerResponse<T>> typeReference) {
        super(typeReference);
    }

    public T dataFrom(String resource) {
        return from(resource).getData();
    }

    public T dataFrom(URL url) {
        return from(url).getData();
    }

    public T dataOf(String json) {
        return of(json).getData();
    }
}
