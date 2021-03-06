package info.xiancloud.cache.service.unit.object;

import info.xiancloud.cache.CacheOperateManager;
import info.xiancloud.cache.redis.Redis;
import info.xiancloud.cache.redis.operate.ObjectCacheOperate;
import info.xiancloud.cache.service.CacheGroup;
import info.xiancloud.plugin.*;
import info.xiancloud.plugin.message.UnitResponse;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.support.cache.CacheConfigBean;
import redis.clients.jedis.Jedis;

/**
 * 缓存
 *
 * @author John_zero
 */
public class CacheObjectUnit implements Unit {
    @Override
    public String getName() {
        return "cacheObject";
    }

    @Override
    public Group getGroup() {
        return CacheGroup.singleton;
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create().setDescription("存储缓存").setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("key", Object.class, "缓存的关键字", REQUIRED)
                .add("value", Object.class, "缓存的内容", REQUIRED)
                .add("timeout", Integer.class, "超时时间, 单位: 秒", NOT_REQUIRED)
                .add("cacheConfig", CacheConfigBean.class, "", NOT_REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        Object value = msg.getArgMap().get("value");
        String key = msg.getArgMap().get("key").toString();
        CacheConfigBean cacheConfigBean = msg.get("cacheConfig", CacheConfigBean.class);

        try (Jedis jedis = Redis.useDataSource(cacheConfigBean).getResource()) {
            if (msg.getArgMap().containsKey("timeout")) {
                int timeout = CacheOperateManager.correctionTimeout(msg.get("timeout", int.class));
                ObjectCacheOperate.setex(jedis, key, value, timeout);
            } else {
                ObjectCacheOperate.set(jedis, key, value);
            }
        } catch (Exception e) {
            return UnitResponse.exception(e);
        }
        return UnitResponse.success();
    }

}
