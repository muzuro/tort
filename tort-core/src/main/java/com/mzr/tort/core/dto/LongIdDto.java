package com.mzr.tort.core.dto;

public class LongIdDto implements IdentifiedDto {

    private Long id;
    
    public LongIdDto() {
    }

    public LongIdDto(Long aId) {
        id = aId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        LongIdDto casted = (LongIdDto) obj;
        
        if (id == null || casted.getId() == null) {
            return this == obj;
        }
        
        return (obj instanceof LongIdDto) && id.equals(casted.getId());
    }
    
}
