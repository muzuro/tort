package com.mzr.tort.core.extractor.param;

import org.hibernate.criterion.Criterion;

public class FetchParam extends Param {
    private final Criterion criterion;
    
    /**
     * Условие будет применятся к корню
     * @param aCriterion условие
     */
    public FetchParam(Criterion aCriterion) {
        super("");
        criterion = aCriterion;
    }
    
    /**
     * @param aPath путь к полю, для которого указываем условие
     * @param aCriterion условие
     */
    public FetchParam(String aPath, Criterion aCriterion) {
        super(aPath);
        criterion = aCriterion;
    }
    
    public Criterion getCriterion() {
        return criterion;
    }
}
