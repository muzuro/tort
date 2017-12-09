package com.mzr.tort.core;

import com.mzr.tort.core.extractor.param.SearchParam;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * User: kmalykh
 * Date: 18.03.15
 */
public class SearchUtils {

    private static final Logger logger = LoggerFactory.getLogger(SearchUtils.class);

    public static Conjunction createCondition(String filter, List<String> fields) {
        List<SearchParam> searchParams = new ArrayList<>(fields.size());
        for (String field : fields) {
            searchParams.add(new SearchParam(field));
        }
        return createConditionByType(filter, searchParams);
    }

    public static Conjunction createConditionByType(String filter, List<SearchParam> fields) {
        Conjunction and = Restrictions.conjunction();

        String[] searchList = filter.split(" ");
        for (String search : searchList) {
            Disjunction or = Restrictions.disjunction();

            for (SearchParam field : fields) {
                if (Objects.nonNull(field.getType())) {
                    if (field.getType() instanceof IntegerType) {

                        try {
                            Integer i = Integer.valueOf(search);
                            or.add(Restrictions.eq(field.getPath(), i));
                        } catch (NumberFormatException e) {
                            logger.warn("Not a number in search");
                        }
                    }
                } else {
                    or.add(Restrictions.ilike(field.getPath(), search, MatchMode.ANYWHERE));
                }
            }

            and.add(or);
        }

        return and;
    }

}
