package com.stk123.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "STK_NEWS")
@Setter
@Getter
public class StkNewsEntity {
    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    private Integer id;

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    private String code;

    @Basic
    @Column(name = "TYPE", nullable = true, precision = 0)
    private Integer type;

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    private Date insertTime;

    @Basic
    @Column(name = "CARE_FLAG", nullable = true, precision = 0)
    private Integer careFlag;

    @Basic
    @Column(name = "INFO", nullable = true, length = 4000)
    private String info;

    @Basic
    @Column(name = "TITLE", nullable = true, length = 1000)
    private String title;

    @Basic
    @Column(name = "URL_SOURCE", nullable = true, length = 1000)
    private String urlSource;

    @Basic
    @Column(name = "URL_TARGET", nullable = true, length = 1000)
    private String urlTarget;

    @Basic
    @Column(name = "INFO_CREATE_TIME", nullable = true)
    private Date infoCreateTime;

    @Transient
    private StkDictionaryEntity stkDictionaryEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkNewsEntity that = (StkNewsEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

//    @ManyToOne
//    @JoinColumn(name = "CODE", referencedColumnName = "CODE")
//    public StkEntity getStkByCode() {
//        return stkByCode;
//    }
//
//    public void setStkByCode(StkEntity stkByCode) {
//        this.stkByCode = stkByCode;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "TYPE", referencedColumnName = "TYPE")
//    public StkImportInfoTypeEntity getStkImportInfoTypeByType() {
//        return stkImportInfoTypeByType;
//    }
//
//    public void setStkImportInfoTypeByType(StkImportInfoTypeEntity stkImportInfoTypeByType) {
//        this.stkImportInfoTypeByType = stkImportInfoTypeByType;
//    }
}
