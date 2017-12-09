package com.mzr.tort.core.change.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectChange extends Change {

    private List<Change> fields = new ArrayList<>();

    public ObjectChange(String aName, List<Change> aFields) {
        super(aName);
        fields = aFields;
    }
    
    public List<Change> getFields() {
        return fields;
    }

    public void setFields(List<Change> aChilds) {
        fields = aChilds;
    }
    
}
