package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_DICTIONARY")
@IdClass(StkDictionaryEntity.CompositeKey.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class StkDictionaryEntity implements Serializable {
    @Id
    @Column(name = "TYPE")
    @JsonView(View.Default.class)
    private Integer type;

    @Id
    @Column(name = "KEY", length = 40)
    @JsonView(View.Default.class)
    private String key;

    @Column(name = "TEXT", length = 200)
    @JsonView(View.Default.class)
    private String text;

    @Column(name = "REMARK", length = 400)
    private String remark;

    @Column(name = "PARAM", length = 200)
    private String param;

    @Column(name = "PARAM_2", length = 100)
    private String param2;
    @Column(name = "PARAM_3", length = 100)
    private String param3;
    @Column(name = "PARAM_4", length = 100)
    private String param4;
    @Column(name = "PARAM_5", length = 100)
    private String param5;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkDictionaryEntity that = (StkDictionaryEntity) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, key);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CompositeKey implements Serializable {
        private Integer type;
        private String key;
    }
}
