package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "STK_DICTIONARY_HEADER")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class StkDictionaryHeaderEntity implements Serializable {
    @Id
    @Column(name = "TYPE")
    @JsonView(View.Default.class)
    private Integer type;

    @Column(name = "NAME", length = 400)
    @JsonView(View.Default.class)
    private String name;

    @Column(name = "STATUS")
    @JsonView(View.Default.class)
    private Integer status;

    @Column(name = "INSERT_TIME")
    @JsonView(View.Default.class)
    private Date insertTime;

    @Column(name = "UPDATE_TIME")
    @JsonView(View.Default.class)
    private Date updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkDictionaryHeaderEntity that = (StkDictionaryHeaderEntity) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }


}
