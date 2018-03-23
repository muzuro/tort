package com.mzr.tort.core.dto;

import java.util.Date;

import com.mzr.tort.core.domain.Finishable;

public class FinishableLongIdDto extends LongIdDto implements Finishable {

    private Date finishTime;

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

}
