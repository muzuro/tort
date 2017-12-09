package com.mzr.tort.core;

import java.util.List;

public class StringHelper {

    public static boolean containsAllCaseInsensitive(String str, List<String> queries) {
        final String slower = str.toLowerCase();
        for (String q : queries) {
            if (!slower.contains(q.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

}