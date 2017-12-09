package com.mzr.tort.core.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public class FinishableLongIdEntity extends LongIdEntity implements Finishable {

    private Date finishTime;

    @Column
    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
}
