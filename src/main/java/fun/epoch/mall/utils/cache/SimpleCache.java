package fun.epoch.mall.utils.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.TimeUnit;

/**
 * 简单缓存工具
 */
public class SimpleCache {
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder()
            .initialCapacity(1000)  // 缓存的初始容量 1000
            .maximumSize(10000)     // 超过最大缓存容量 10000 时采用 LRU 算法移除缓存项
            .expireAfterAccess(30, TimeUnit.MINUTES)    // 缓存有效期30分钟
            .build(new CacheLoader<String, String>() {
                @Override
                @ParametersAreNonnullByDefault
                public String load(String s) {
                    return null; // 默认的数据加载实现，当调用 get 取值的时候，如果 key 没有对应的值，就调用这个方法进行加载
                }
            });

    public static void put(String key, String value) {
        localCache.put(key, value);
    }

    public static String get(String key) {
        return localCache.getIfPresent(key);
    }

    public static void clearAll() {
        localCache.invalidateAll();
    }
}
