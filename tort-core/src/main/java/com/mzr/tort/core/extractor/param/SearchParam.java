package com.mzr.tort.core.extractor.param;

import org.hibernate.type.Type;

public class SearchParam extends Param {

    private org.hibernate.type.Type type;

    public SearchParam(String aPath) {
        super(aPath);
    }

    public SearchParam(String aPath, org.hibernate.type.Type type) {
        super(aPath);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
