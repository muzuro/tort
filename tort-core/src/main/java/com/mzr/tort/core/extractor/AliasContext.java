package com.mzr.tort.core.extractor;

import com.mzr.tort.core.extractor.param.AliasParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 */
public class AliasContext {

    //name -> path
    private Map<String, String> aliases = new HashMap<>();
    
    public void add(String aName, String aPath) {
        aliases.put(aName, aPath);
    }
    
    public List<AliasParam> getParams() {
        return aliases.entrySet().stream().map(e->new AliasParam(e.getValue(), e.getKey())).collect(Collectors.toList());
    }
    
}
