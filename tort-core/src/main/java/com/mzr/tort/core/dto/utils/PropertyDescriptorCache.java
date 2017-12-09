package com.mzr.tort.core.dto.utils;

import java.util.List;
import java.util.Map;

class PropertyDescriptorCache {
    private final List<Prop> propertyDescriptors;
    private final Map<String, Prop> propertyDescriptorMap;

    PropertyDescriptorCache(List<Prop> propertyDescriptors,
                            Map<String, Prop> propertyDescriptorMap) {
        this.propertyDescriptors = propertyDescriptors;
        this.propertyDescriptorMap = propertyDescriptorMap;
    }

    public List<Prop> getPropertyDescriptors() {
        return propertyDescriptors;
    }

    public Map<String, Prop> getPropertyDescriptorMap() {
        return propertyDescriptorMap;
    }
}
