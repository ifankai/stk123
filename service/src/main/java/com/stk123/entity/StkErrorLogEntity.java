package com.stk123.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_ERROR_LOG")
@Setter
@Getter
public class StkErrorLogEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_error_log_id")
    @SequenceGenerator(name="s_error_log_id", sequenceName="s_error_log_id", allocationSize = 1)
    private Integer id;

    @Column(name = "CODE", nullable = true, length = 10)
    private String code;

    @Column(name = "TEXT", nullable = true)
    private String text;

    @Column(name = "ERROR", nullable = true)
    private String error;

    @Column(name = "INSERT_TIME", nullable = true)
    private Time insertTime;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkErrorLogEntity that = (StkErrorLogEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
