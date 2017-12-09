package com.mzr.tort.core.extractor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapped {
    
    /**
     * @return имя проперти(также можно указать путь к проперти) которое соответствует этому полю в дто 
     */
    public String value();

}
