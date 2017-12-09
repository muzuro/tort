package com.mzr.tort.core.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Файл в бинарном виде
 *
 *
 * @date 27.01.2015
 */
@Entity
@Table(name = "FILE_DATA")
@AttributeOverride(name = "id", column = @Column(name = "FILE_DATA_ID"))
public class FileData extends UUIDEntity {

    @Column(name = "DATA")
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
