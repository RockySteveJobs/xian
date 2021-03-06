package info.xiancloud.cache.service.unit.map;

import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.MapCacheOperate;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

/**
 * Map Clear
 *
 * @author John_zero
 */
public class CacheMapClearUnit implements Unit {
    @Override
    public String getName() {
        return "cacheMapClear";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("Map Clear").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input().add("key", String.class, "缓存的关键字", REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        String key = msg.getArgMap().get("key").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        long result = 0L;
        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            result = MapCacheOperate.removeAll(jedis, key);
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
        return UnitResponse.success(result);
    }

}
