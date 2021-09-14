package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "STK_KEYWORD")
@Getter
@Setter
public class StkKeywordEntity {

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_keyword_id")
    @SequenceGenerator(name="s_keyword_id", sequenceName="s_keyword_id", allocationSize = 1)
    private Long id;

    @Basic
    @Column(name = "NAME", nullable = true, length = 200)
    @JsonView(View.Default.class)
    private String name;

    @Basic
    @Column(name = "BOOST", nullable = true, precision = 0)
    private Integer boost;

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    private Date insertTime;

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    @JsonView(View.Default.class)
    private Integer status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkKeywordEntity that = (StkKeywordEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

//    @OneToMany(mappedBy = "stkKeywordByKeywordId")
//    public Collection<StkKeywordLinkEntity> getStkKeywordLinksById() {
//        return stkKeywordLinksById;
//    }
//
//    public void setStkKeywordLinksById(Collection<StkKeywordLinkEntity> stkKeywordLinksById) {
//        this.stkKeywordLinksById = stkKeywordLinksById;
//    }
}
