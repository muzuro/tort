package com.mzr.tort.core.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @date: 26.10.2017
 */
public class LocalDateInterval implements Serializable {

    private LocalDate start;

    private LocalDate end;

    /**
     * ctor
     *
     * @param start
     * @param end
     */
    public LocalDateInterval(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalDateInterval dateInterval = (LocalDateInterval) o;

        return Objects.equals(start, dateInterval.start) && Objects.equals(end, dateInterval.end);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(17);
        sb.append(start).append("/").append(end);
        return sb.toString();
    }
}
