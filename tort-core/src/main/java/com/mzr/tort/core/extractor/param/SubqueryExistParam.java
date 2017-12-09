package com.mzr.tort.core.extractor.param;
import com.google.common.collect.Lists;
import com.mzr.tort.core.domain.IdentifiedEntity;

import java.util.List;

public class SubqueryExistParam extends AliasParam {

    private final Class<? extends IdentifiedEntity> subqueryClass;
    private final String rootReference;
    private final List<FetchParam> subqueryParams;
    
    public SubqueryExistParam(Class<? extends IdentifiedEntity> aClass, String aAlias, String aRootReference, FetchParam aSubqueryParam) {
        super("", aAlias);
        subqueryClass = aClass;
        rootReference = aRootReference;
        subqueryParams = Lists.newArrayList(aSubqueryParam);
    }
    
    public SubqueryExistParam(Class<? extends IdentifiedEntity> aClass, String aAlias, String aRootReference, List<FetchParam> aSubqueryParams) {
        super("", aAlias);
        subqueryClass = aClass;
        rootReference = aRootReference;
        subqueryParams = aSubqueryParams;
    }

    public String getRootReference() {
        return rootReference;
    }

    public Class<? extends IdentifiedEntity> getSubqueryClass() {
        return subqueryClass;
    }

    public List<FetchParam> getSubqueryParams() {
        return subqueryParams;
    }

}
