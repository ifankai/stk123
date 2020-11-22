package com.stk123.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "STK_INDUSTRY_TYPE", schema = "STK", catalog = "")
public class StkIndustryTypeEntity {
    private int id;
    private String name;
    private String source;
    private Integer careFlag;
    private Integer parentId;
    private String usName;
    private String code;
    private String parentCode;

    private List<StkDataIndustryPeEntity> stkDataIndustryPeEntityList;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME", nullable = true, length = 200)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "SOURCE", nullable = true, length = 20)
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Basic
    @Column(name = "CARE_FLAG", nullable = true, precision = 0)
    public Integer getCareFlag() {
        return careFlag;
    }

    public void setCareFlag(Integer careFlag) {
        this.careFlag = careFlag;
    }

    @Basic
    @Column(name = "PARENT_ID", nullable = true, precision = 0)
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "US_NAME", nullable = true, length = 200)
    public String getUsName() {
        return usName;
    }

    public void setUsName(String usName) {
        this.usName = usName;
    }

    @Basic
    @Column(name = "CODE", nullable = true, length = 20)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "PARENT_CODE", nullable = true, length = 20)
    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    /**
     *  @fankai: LazyInitializationException: could not initialize proxy - no Session
     *  在取数据的时候，此时session已经关闭了，而保持session的话，需要事务@Transactional
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "INDUSTRY_ID")
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
