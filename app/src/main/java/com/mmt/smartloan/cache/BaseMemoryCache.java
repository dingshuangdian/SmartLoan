package com.mmt.smartloan.cache;

import android.util.LruCache;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class BaseMemoryCache extends LruCache<String, Object> {
    /**
     * maxSize for caches that do not override {@link #sizeOf}, this is
     * the maximum number of entries in the cache. For all other caches,
     * this is the maximum sum of the sizes of the entries in this cache.
     */
    public BaseMemoryCache() {
        super(Integer.MAX_VALUE);
    }

    public String getString(String key) {
        Object object = get(key);
        return object == null ? null : object.toString();
    }

    public Integer getInteger(String key) {
        Integer value = null;
        try {
            value = Integer.valueOf(getString(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public Long getLong(String key) {
        Long value = null;
        try {
            value = Long.valueOf(getString(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public Double getDouble(String key) {
        Double value = null;
        try {
            value = Double.valueOf(getString(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public Float getFloat(String key) {
        Float value = null;
        try {
            value = Float.valueOf(getString(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public Boolean getBoolean(String key) {
        Boolean value = null;
        try {
            value = Boolean.valueOf(getString(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
