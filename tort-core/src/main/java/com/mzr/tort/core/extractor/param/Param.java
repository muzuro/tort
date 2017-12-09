package com.mzr.tort.core.extractor.param;

public abstract class Param {

    private final String path;
    
    public Param(String aPath) {
        path = aPath;
    }

    public String getPath() {
        return path;
    }
    
}
