package com.mzr.tort.core.domain;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @date 08/12/16
 */
public class Interval implements Serializable {

    private Time start;

    private Time end;

    /**
     * ctor
     *
     * @param start
     * @param end
     */
    public Interval(Time start, Time end) {
        this.start = start;
        this.end = end;
    }

    public Interval(Date start, Date end) {
        if (Objects.nonNull(start)) {
            this.start = new Time(start.getTime());
        }
        if (Objects.nonNull(end)) {
            this.end = new Time(end.getTime());
        }
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;

        } else if (o instanceof Interval) {
            Interval otherInterval = Interval.class.cast(o);
            return DateHelper.isEquals(start, otherInterval.start) && DateHelper.isEquals(end, otherInterval.end);

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(start).append(end).toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(17);
        sb.append(start).append("/").append(end);
        return sb.toString();
    }
}

