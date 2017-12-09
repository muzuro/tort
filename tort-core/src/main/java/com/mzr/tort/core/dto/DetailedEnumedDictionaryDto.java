package com.mzr.tort.core.dto;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @date 25.04.16
 */
public class DetailedEnumedDictionaryDto extends StyledEnumDictionaryDto {

    private Map details = new HashMap<>();

    public Map getDetails() {
        return details;
    }

    protected void setDetails(Map details) {
        this.details = details;
    }
}
