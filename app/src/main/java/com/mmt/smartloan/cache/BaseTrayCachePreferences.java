package com.mmt.smartloan.cache;

import android.content.Context;
import android.util.LruCache;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.mmt.smartloan.utils.DLog;

import net.grandcentrix.tray.TrayPreferences;
import net.grandcentrix.tray.core.ItemNotFoundException;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class BaseTrayCachePreferences extends TrayPreferences {
    private static final String TAG = BaseTrayCachePreferences.class.getCanonicalName();
    private LruCache<String, Object> mLruCache;

    public BaseTrayCachePreferences(@NonNull Context context, @NonNull String module, int version) {
        super(context, module, version);
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 32);
        mLruCache = new LruCache<String, Object>(cacheSize) {
            @Override
            protected Object create(String key) {
                DLog.v(TAG, "cache未命中，从tray获取: " + key);
                Object value = null;
                try {
                    value = BaseTrayCachePreferences.super.getString(key);
                    DLog.v(TAG, "tray命中,此值为" + value);
                } catch (ItemNotFoundException e) {
                    DLog.v(TAG, "tray未命中,此值为null");
                }
                return value;
            }
        };
    }

    /*------------------------------------ start put -------------------------------------------*/

    /**
     * 同步方式写入
     */
    public boolean putSync(@NonNull final String key, final String value) {
        putCache(key, value);
        return BaseTrayCachePreferences.super.put(key, value);
    }

    /**
     * 同步方式写入
     */
    public boolean putSync(@NonNull final String key, final int value) {
        putCache(key, value);
        return BaseTrayCachePreferences.super.put(key, value);
    }

    /**
     * 写入JSON实体对象
     */
    public void put(@NonNull final String key, final Object object) {
        new Thread(() -> put(key, JSON.toJSONString(object))).start();
    }

    @Override
    public boolean put(@NonNull final String key, final String value) {
        putCache(key, value);
        new Thread(() -> BaseTrayCachePreferences.super.put(key, value)).start();
        return true;
    }

    @Override
    public boolean put(@NonNull final String key, final int value) {
        putCache(key, value);
        new Thread(() -> BaseTrayCachePreferences.super.put(key, value)).start();
        return true;
    }

    @Override
    public boolean put(@NonNull final String key, final float value) {
        putCache(key, value);
        new Thread(() -> BaseTrayCachePreferences.super.put(key, value)).start();
        return true;
    }

    @Override
    public boolean put(@NonNull final String key, final long value) {
        putCache(key, value);
        new Thread(() -> BaseTrayCachePreferences.super.put(key, value)).start();
        return true;
    }

    @Override
    public boolean put(@NonNull final String key, final boolean value) {
        putCache(key, value);
        new Thread(() -> BaseTrayCachePreferences.super.put(key, value)).start();
        return true;
    }

    /*------------------------------------ end put -------------------------------------------*/
    /*------------------------------------ start get -------------------------------------------*/
    @Override
    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        try {
            return super.getBoolean(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public boolean getBoolean(@NonNull final String key) throws ItemNotFoundException {
        final String value = getString(key);
        if (value == null) {
            throw new ItemNotFoundException();
        }
        return Boolean.parseBoolean(value);
    }

    @Override
    public float getFloat(@NonNull String key, float defaultValue) {
        try {
            return super.getFloat(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public int getInt(@NonNull String key, int defaultValue) {
        try {
            return super.getInt(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }

    }

    @Override
    public long getLong(@NonNull String key, long defaultValue) {
        try {
            return super.getLong(key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String getString(@NonNull final String key, final String defaultValue) {
        try {
            String value = getString(key);
            return value == null ? defaultValue : value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String getString(@NonNull String key) {
        DLog.v(TAG, "cache获取============================================================");
        Object o = getCache(key);
        return o == null ? null : o.toString();
    }

    /**
     * 获取实体对象
     */
    public <T> T getObject(@NonNull String key, Class<T> clazz) {
        return JSON.parseObject(getString(key), clazz);
    }

    /*------------------------------------ end get -------------------------------------------*/
    /*------------------------------------ start remove/wipe/clear -------------------------------------------*/
    @Override
    public boolean remove(@NonNull final String key) {
        DLog.v(TAG, "cache移除: " + key);
        mLruCache.remove(key);
        new Thread(() -> {
            DLog.v(TAG, "tray remove: " + key);
            BaseTrayCachePreferences.super.remove(key);
        }).start();
        return true;
    }

    /**
     * 删除数据库
     */
    @Override
    public boolean wipe() {
        clearMemoryCache();
        new Thread(() -> {
            DLog.v(TAG, "tray wipe============================================================");
            BaseTrayCachePreferences.super.wipe();
        }).start();
        return true;
    }

    /*------------------------------------ end remove/wipe/clear -------------------------------------------*/

    /**
     * 放入缓存
     *
     * @param key   key
     * @param value value
     */
    private void putCache(@NonNull final String key, final Object value) {
        DLog.v(TAG, "cache写入============================================================");
        DLog.v(TAG, "cache写入: " + key + "," + value);
        if (value == null) {
            mLruCache.remove(key);
        } else {
            mLruCache.put(key, value);
        }
    }

    /**
     * 从缓存获取
     */
    public Object getCache(@NonNull String key) {
        DLog.v(TAG, "cache获取: " + key);
        Object obj = mLruCache.get(key);
        DLog.v(TAG, "cache获取到: " + key + "," + obj);
        return obj;
    }

    /**
     * 清空缓存
     *
     * @see BaseTrayCachePreferences#clearMemoryCache()
     * @deprecated
     */
    public void clearCache() {
        DLog.v(TAG, "cache清空============================================================");
        mLruCache.evictAll();
    }

    /**
     * 清空缓存
     */
    public void clearMemoryCache() {
        DLog.v(TAG, "cache清空============================================================");
        mLruCache.evictAll();
    }

    /**
     * 清理内存缓存&&数据库缓存
     */
    @Override
    public boolean clear() {
        clearMemoryCache();
        new Thread(() -> {
            DLog.v(TAG, "tray clear============================================================");
            BaseTrayCachePreferences.super.clear();
        }).start();
        return true;
    }

    public LruCache<String, Object> getLruCache() {
        return mLruCache;
    }
}

