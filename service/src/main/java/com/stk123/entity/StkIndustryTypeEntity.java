package com.stk123.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "STK_INDUSTRY_TYPE")
@Getter
@Setter
public class StkIndustryTypeEntity {
    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    private Integer id;

    @Basic
    @Column(name = "NAME", nullable = true, length = 200)
    private String name;

    @Basic
    @Column(name = "SOURCE", nullable = true, length = 20)
    private String source;

    @Basic
    @Column(name = "CARE_FLAG", nullable = true, precision = 0)
    private Integer careFlag;

    @Basic
    @Column(name = "PARENT_ID", nullable = true, precision = 0)
    private Integer parentId;

    @Basic
    @Column(name = "US_NAME", nullable = true, length = 200)
    private String usName;

    @Basic
    @Column(name = "CODE", nullable = true, length = 20)
    private String code;

    @Basic
    @Column(name = "PARENT_CODE", nullable = true, length = 20)
    private String parentCode;



    /**
     *  @fankai: LazyInitializationException: could not initialize proxy - no Session
     *  在取数据的时候，此时session已经关闭了，而保持session的话，需要事务@Transactional
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "INDUSTRY_ID")
    private List<StkDataIndustryPeEntity> stkDataIndustryPeEntityList;

    public List<StkDataIndustryPeEntity> getStkDataIndustryPeEntityList() {
        return stkDataIndustryPeEntityList;
    }

    public void setStkDataIndustryPeEntityList(List<StkDataIndustryPeEntity> stkDataIndustryPeEntityList) {
        this.stkDataIndustryPeEntityList = stkDataIndustryPeEntityList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkIndustryTypeEntity that = (StkIndustryTypeEntity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(source, that.source) &&
                Objects.equals(careFlag, that.careFlag) &&
                Objects.equals(parentId, that.parentId) &&
                Objects.equals(usName, that.usName) &&
                Objects.equals(code, that.code) &&
                Objects.equals(parentCode, that.parentCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, source, careFlag, parentId, usName, code, parentCode);
    }
}
