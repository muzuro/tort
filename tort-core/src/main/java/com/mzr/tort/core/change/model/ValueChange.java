package com.mzr.tort.core.change.model;

public class ValueChange extends Change {

    private String old;
    private String young;
    public ValueChange(String aValueName, String aOld, String aYoung) {
        super(aValueName);
        old = aOld;
        young = aYoung;
    }
    public String getOld() {
        return old;
    }
    public void setOld(String aOld) {
        old = aOld;
    }
    public String getYoung() {
        return young;
    }
    public void setYoung(String aYoung) {
        young = aYoung;
    }
    
}
