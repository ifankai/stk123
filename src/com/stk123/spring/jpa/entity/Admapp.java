package com.stk123.spring.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ADMAPP")
public class Admapp implements Serializable {

    @Id
    private String rowid;
    private String adapplication;
    private String adacountry;
    private String adaedesc;
    private String adalink;
    private String adastat;
    private Date adaupd;
    private String adausrupd;
    private String adausrcre;
    private Date adacre;

    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getAdapplication() {
        return adapplication;
    }

    public void setAdapplication(String adapplication) {
        this.adapplication = adapplication;
    }

    public String getAdacountry() {
        return adacountry;
    }

    public void setAdacountry(String adacountry) {
        this.adacountry = adacountry;
    }

    public String getAdaedesc() {
        return adaedesc;
    }

    public void setAdaedesc(String adaedesc) {
        this.adaedesc = adaedesc;
    }

    public String getAdalink() {
        return adalink;
    }

    public void setAdalink(String adalink) {
        this.adalink = adalink;
    }

    public String getAdastat() {
        return adastat;
    }

    public void setAdastat(String adastat) {
        this.adastat = adastat;
    }

    public Date getAdaupd() {
        return adaupd;
    }

    public void setAdaupd(Date adaupd) {
        this.adaupd = adaupd;
    }

    public String getAdausrupd() {
        return adausrupd;
    }

    public void setAdausrupd(String adausrupd) {
        this.adausrupd = adausrupd;
    }

    public String getAdausrcre() {
        return adausrcre;
    }

    public void setAdausrcre(String adausrcre) {
        this.adausrcre = adausrcre;
    }

    public Date getAdacre() {
        return adacre;
    }

    public void setAdacre(Date adacre) {
        this.adacre = adacre;
    }
}
