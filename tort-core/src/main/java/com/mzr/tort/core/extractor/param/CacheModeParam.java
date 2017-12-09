package com.mzr.tort.core.extractor.param;

import org.hibernate.CacheMode;

/**
 *
 * @date 31.05.2016
 */
public class CacheModeParam extends Param {

    private final CacheMode cacheMode;

    public CacheModeParam(CacheMode cacheMode) {
        super("");

        this.cacheMode = cacheMode;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }
}
