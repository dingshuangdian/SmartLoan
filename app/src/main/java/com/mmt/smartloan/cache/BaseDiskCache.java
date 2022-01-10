package com.mmt.smartloan.cache;

import androidx.annotation.NonNull;

import com.mmt.smartloan.utils.ContextHolder;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class BaseDiskCache extends BaseTrayCachePreferences {
    public BaseDiskCache(@NonNull String module, int version) {
        super(ContextHolder.getContext(), module, version);
    }

    @Override
    protected void onCreate(final int initialVersion) {
        super.onCreate(initialVersion);
    }

    @Override
    protected void onUpgrade(final int oldVersion, final int newVersion) {
        super.onUpgrade(oldVersion, newVersion);
    }

    @Override
    protected void onDowngrade(final int oldVersion, final int newVersion) {
        super.onDowngrade(oldVersion, newVersion);
    }
}
