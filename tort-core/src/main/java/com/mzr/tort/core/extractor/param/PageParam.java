package com.mzr.tort.core.extractor.param;

public class PageParam extends Param {
    
    private final Integer from;
    private final Integer count;

    public PageParam(Integer aFrom, Integer aCount) {
        super("");
        from = aFrom;
        count = aCount;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getFrom() {
        return from;
    }

}
