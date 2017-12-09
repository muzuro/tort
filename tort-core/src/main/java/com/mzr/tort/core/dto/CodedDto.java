package com.mzr.tort.core.dto;

/**
 *
 * @date: 15.05.15 14:16
 */
public class CodedDto implements IdentifiedDto {

    private String code;

    public String getId() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        code = code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        CodedDto casted = (CodedDto) obj;

        if (code == null || casted.getId() == null) {
            return this == obj;
        }

        return (obj instanceof CodedDto) && code.equals(casted.getId());
    }

}