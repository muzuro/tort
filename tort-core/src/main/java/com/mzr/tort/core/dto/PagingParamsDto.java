package com.mzr.tort.core.dto;

import java.util.HashMap;
import java.util.Map;

public class PagingParamsDto {
    private Integer fromNum;
    private Integer count; 
    private String order;
    private Map<String, Object> params = new HashMap<>();
    public Integer getFromNum() {
        return fromNum;
    }
    public void setFromNum(Integer aFromNum) {
        fromNum = aFromNum;
    }
    public Integer getCount() {
        return count;
    }
    public void setCount(Integer aCount) {
        count = aCount;
    }
    public String getOrder() {
        return order;
    }
    public void setOrder(String aOrder) {
        order = aOrder;
    }
    public Map<String, Object> getParams() {
        return params;
    }
    public void setParams(Map<String, Object> aParams) {
        params = aParams;
    }
}
