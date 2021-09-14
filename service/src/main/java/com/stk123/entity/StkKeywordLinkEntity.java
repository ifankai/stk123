package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "STK_KEYWORD_LINK")
@Getter
@Setter
public class StkKeywordLinkEntity {
    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_keyword_link_id")
    @SequenceGenerator(name="s_keyword_link_id", sequenceName="s_keyword_link_id", allocationSize = 1)
    private Long id;

    @Basic
    @Column(name = "CODE", nullable = true, length = 20)
    private String code;

    @Basic
    @Column(name = "CODE_TYPE", nullable = true, precision = 0)
    private Integer codeType;

    @Basic
    @Column(name = "KEYWORD_ID", nullable = true, precision = 0)
    @JsonView(View.Default.class)
    private Long keywordId;

    @Basic
    @Column(name = "BOOST", nullable = true, precision = 0)
    private Integer boost;

    @Basic
    @Column(name = "LINK_TYPE", nullable = true, precision = 0)
    @JsonView(View.Default.class)
    private Integer linkType;

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    private Date insertTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "KEYWORD_ID", insertable = false, updatable = false)
    @JsonView(View.Default.class)
    private StkKeywordEntity keyword;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkKeywordLinkEntity that = (StkKeywordLinkEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

//    @ManyToOne
//    @JoinColumn(name = "KEYWORD_ID", referencedColumnName = "ID")
//    public StkKeywordEntity getStkKeywordByKeywordId() {
//        return stkKeywordByKeywordId;
//    }
//
//    public void setStkKeywordByKeywordId(StkKeywordEntity stkKeywordByKeywordId) {
//        this.stkKeywordByKeywordId = stkKeywordByKeywordId;
//    }
}
