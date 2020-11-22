package com.stk123.spring.jpa.entity;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "ADMAPP_LOCAL2")
public class AdmappLocal2 {
    @Id
    private String rowid;
    private String adapplication;
    private String adacountry;
    private String adaedesc;
    private String adalink;
    private String adastat;
    private Time adaupd;
    private String adausrupd;
    private String adausrcre;
    private Time adacre;

    @Column(name = "ROWID")
    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    @Basic
    @Column(name = "ADAPPLICATION", nullable = true, length = 255)
    public String getAdapplication() {
        return adapplication;
    }

    public void setAdapplication(String adapplication) {
        this.adapplication = adapplication;
    }

    @Basic
    @Column(name = "ADACOUNTRY", nullable = true, length = 255)
    public String getAdacountry() {
        return adacountry;
    }

    public void setAdacountry(String adacountry) {
        this.adacountry = adacountry;
    }

    @Basic
    @Column(name = "ADAEDESC", nullable = true, length = 255)
    public String getAdaedesc() {
        return adaedesc;
    }

    public void setAdaedesc(String adaedesc) {
        this.adaedesc = adaedesc;
    }

    @Basic
    @Column(name = "ADALINK", nullable = true, length = 255)
    public String getAdalink() {
        return adalink;
    }

    public void setAdalink(String adalink) {
        this.adalink = adalink;
    }

    @Basic
    @Column(name = "ADASTAT", nullable = true, length = 4)
    public String getAdastat() {
        return adastat;
    }

    public void setAdastat(String adastat) {
        this.adastat = adastat;
    }

    @Basic
    @Column(name = "ADAUPD", nullable = true)
    public Time getAdaupd() {
        return adaupd;
    }

    public void setAdaupd(Time adaupd) {
        this.adaupd = adaupd;
    }

    @Basic
    @Column(name = "ADAUSRUPD", nullable = true, length = 1020)
    public String getAdausrupd() {
        return adausrupd;
    }

    public void setAdausrupd(String adausrupd) {
        this.adausrupd = adausrupd;
    }

    @Basic
    @Column(name = "ADAUSRCRE", nullable = true, length = 1020)
    public String getAdausrcre() {
        return adausrcre;
    }

    public void setAdausrcre(String adausrcre) {
        this.adausrcre = adausrcre;
    }

    @Basic
    @Column(name = "ADACRE", nullable = true)
    public Time getAdacre() {
        return adacre;
    }

    public void setAdacre(Time adacre) {
        this.adacre = adacre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdmappLocal2 that = (AdmappLocal2) o;

        if (adapplication != null ? !adapplication.equals(that.adapplication) : that.adapplication != null)
            return false;
        if (adacountry != null ? !adacountry.equals(that.adacountry) : that.adacountry != null) return false;
        if (adaedesc != null ? !adaedesc.equals(that.adaedesc) : that.adaedesc != null) return false;
        if (adalink != null ? !adalink.equals(that.adalink) : that.adalink != null) return false;
        if (adastat != null ? !adastat.equals(that.adastat) : that.adastat != null) return false;
        if (adaupd != null ? !adaupd.equals(that.adaupd) : that.adaupd != null) return false;
        if (adausrupd != null ? !adausrupd.equals(that.adausrupd) : that.adausrupd != null) return false;
        if (adausrcre != null ? !adausrcre.equals(that.adausrcre) : that.adausrcre != null) return false;
        if (adacre != null ? !adacre.equals(that.adacre) : that.adacre != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = adapplication != null ? adapplication.hashCode() : 0;
        result = 31 * result + (adacountry != null ? adacountry.hashCode() : 0);
        result = 31 * result + (adaedesc != null ? adaedesc.hashCode() : 0);
        result = 31 * result + (adalink != null ? adalink.hashCode() : 0);
        result = 31 * result + (adastat != null ? adastat.hashCode() : 0);
        result = 31 * result + (adaupd != null ? adaupd.hashCode() : 0);
        result = 31 * result + (adausrupd != null ? adausrupd.hashCode() : 0);
        result = 31 * result + (adausrcre != null ? adausrcre.hashCode() : 0);
        result = 31 * result + (adacre != null ? adacre.hashCode() : 0);
        return result;
    }
}
