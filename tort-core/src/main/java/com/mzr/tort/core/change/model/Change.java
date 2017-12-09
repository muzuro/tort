package com.mzr.tort.core.change.model;

public abstract class Change {

    /** имя поля, которое поменялось */
    private String caption;    

    public Change(String aCaption) {
        caption = aCaption;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String aCaption) {
        caption = aCaption;
    }
    
}
