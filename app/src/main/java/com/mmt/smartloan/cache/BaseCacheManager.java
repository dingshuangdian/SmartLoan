package com.mmt.smartloan.cache;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */

/**
 * 三种sp缓存
 * 1.设置后不销毁（系统配置）
 * 2.设置后注销不清空 (用户配置)
 * 3.设置后注销清空 (用户临时配置)
 */
public class BaseCacheManager {
    private static BaseCacheManager mInstance;
    //临时缓存
    private BaseTempPreferences userTempPreferences;
    //系统缓存
    private BaseSystemPreferences systemPreferences;
    //用户缓存
    private BaseUserPreferences userPreferences;
    //内存缓存
    private BaseMemoryCache memoryCache;

    public static void initUserPreferences() {
        getInstance().updateUserPreferences();
    }

    public static void initUserPreferences(String userId) {
        getInstance().updateUserPreferences(userId);
    }

    /**
     * 获取用户临时缓存
     */
    public static BaseTempPreferences getUserTemp() {
        return getInstance().getCurrentUserTmpPreferences();
    }

    /**
     * 获取用户缓存
     */
    public static BaseUserPreferences getUser() {
        return getInstance().getCurrentUserPreferences();
    }

    /**
     * 获取系统缓存
     */
    public static BaseSystemPreferences getSystem() {
        return getInstance().getSystemPreferences();
    }

    /**
     * 获取内存缓存
     */
    public static BaseMemoryCache getMemory() {
        return getInstance().getMemoryCache();
    }

    /**
     * 注销用户缓存
     */
    public static void destroyUserPreferences() {
        //1.清除当前用户临时数据 [内存缓存] && [磁盘缓存]
        getInstance().getCurrentUserTmpPreferences().clear();
        //2.清除当前用户持久数据 [内存缓存]
        getInstance().getCurrentUserPreferences().clearMemoryCache();
        //3.清除内存缓存
        getInstance().getMemoryCache().evictAll();
    }

    private static BaseCacheManager getInstance() {
        if (mInstance == null) {
            synchronized (BaseCacheManager.class) {
                if (mInstance == null) {
                    mInstance = new BaseCacheManager();
                }
            }
        }
        return mInstance;
    }

    public BaseCacheManager() {
        userTempPreferences = new BaseTempPreferences();
        systemPreferences = new BaseSystemPreferences();
        memoryCache = new BaseMemoryCache();
    }

    public BaseTempPreferences getCurrentUserTmpPreferences() {
        return userTempPreferences;
    }

    public BaseSystemPreferences getSystemPreferences() {
        return systemPreferences;
    }

    public BaseMemoryCache getMemoryCache() {
        return memoryCache;
    }

    /**
     * 更新用户缓存
     */
    public void updateUserPreferences() {
        updateUserPreferences(userTempPreferences.getUserId());
    }

    /**
     * 更新用户缓存
     */
    public void updateUserPreferences(String userId) {
        if (userId == null) {
            throw new NullPointerException("BaseCacheManager临时缓存 userId = null");
        }
        this.userPreferences = new BaseUserPreferences(userId);
    }

    /**
     * 获取用户缓存
     */
    public BaseUserPreferences getCurrentUserPreferences() {
        if (userPreferences == null) {
            updateUserPreferences();
        }
        return userPreferences;
    }
}
