package com.mzr.tort.core.extractor.param;

import java.util.function.BiConsumer;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;

public class FetchParam extends Param {

    private final BiConsumer<CriteriaBuilder, FetchParent> criterion;

    /**
     * @param aCriterion
     */
    public FetchParam(BiConsumer<CriteriaBuilder, FetchParent> aCriterion) {
        super("");
        criterion = aCriterion;
    }
    
    /**
     * @param aPath
     * @param aCriterion
     */
    public FetchParam(String aPath, BiConsumer<CriteriaBuilder, FetchParent> aCriterion) {
        super(aPath);
        criterion = aCriterion;
    }
    
    public BiConsumer<CriteriaBuilder, FetchParent> getCriterion() {
        return criterion;
    }
}
