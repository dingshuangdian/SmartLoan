package com.mmt.smartloan.cache;

import androidx.annotation.NonNull;

import com.mmt.smartloan.utils.UserInfoUtils;


/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class BaseUserPreferences extends BaseDiskCache {
    public BaseUserPreferences() {
        this(UserInfoUtils.getUserId());
    }

    public BaseUserPreferences(@NonNull String module) {
        super(module, 1);
    }
}
