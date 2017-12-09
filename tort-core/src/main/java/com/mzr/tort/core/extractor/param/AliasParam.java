package com.mzr.tort.core.extractor.param;

public class AliasParam extends Param {

    private final String alias;

    /**
     * @param aPath путь к полю, для которого указываем алиас 
     * @param aAlias алиас
     */
    public AliasParam(String aPath, String aAlias) {
        super(aPath);
        alias = aAlias;
    }

    public String getAlias() {
        return alias;
    }

}
