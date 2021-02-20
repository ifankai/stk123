package com.stk123.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_INDUSTRY")
@IdClass(StkIndustryEntity.CompositeKey.class)
@Getter
@Setter
public class StkIndustryEntity implements Serializable {

    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    private String code;
    @Id
    @Column(name = "INDUSTRY", nullable = true, precision = 0)
    private Integer industry;

    private StkIndustryTypeEntity stkIndustryTypeEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkIndustryEntity that = (StkIndustryEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(industry, that.industry);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, industry);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CompositeKey implements Serializable {
        private String code;
        private Integer industry;
    }
}
