package com.mzr.tort.core.extractor.param;

import org.hibernate.criterion.Order;

public class OrderParam extends Param {
    
    private final Order order;

    /**
     * @param path       путь к полю, для которого указываем условие
     * @param aCriterion условие
     */
    public OrderParam(Order order) {
        super("");
        this.order = order;
    }

    /**
     * @param path       путь к полю, для которого указываем условие
     * @param aCriterion условие
     */
    public OrderParam(String aPath, Order order) {
        super(aPath);
        this.order = order;
    }


    public Order getOrder() {
        return order;
    }
    
    /**
     * @param order - запрос на сортировку, в формате ng-table  
     * @return orderParam
     */
    public static OrderParam parseNgTable(String order) {
        if (order.contains(".")) {
            String path = order.substring(0, order.lastIndexOf("."));
            if (order.startsWith("-")) {
                return new OrderParam(path.substring(1), Order.desc(order.substring(order.lastIndexOf(".") + 1, order.length())));
            } else {
                return new OrderParam(path.substring(1), Order.asc(order.substring(order.lastIndexOf(".") + 1, order.length())));
            }
        } else if (order.startsWith("-")) {
            return new OrderParam("", Order.desc(order.substring(1)));
        } else {
            return new OrderParam("", Order.asc(order.substring(1)));
        }
    }


}
