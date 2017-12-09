package com.mzr.tort.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *
 * @date 10.07.2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateInterval {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date begin;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date end;

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
