package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_USER", schema = "STK", catalog = "")
public class StkUserEntity {
    private long id;
    private String nickname;
    private String password;
    private String email;
    private String earningSearchParams;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NICKNAME", nullable = true, length = 100)
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Basic
    @Column(name = "PASSWORD", nullable = true, length = 40)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "EMAIL", nullable = true, length = 200)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "EARNING_SEARCH_PARAMS", nullable = true, length = 2000)
    public String getEarningSearchParams() {
        return earningSearchParams;
    }

    public void setEarningSearchParams(String earningSearchParams) {
        this.earningSearchParams = earningSearchParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkUserEntity that = (StkUserEntity) o;
        return id == that.id &&
                Objects.equals(nickname, that.nickname) &&
                Objects.equals(password, that.password) &&
                Objects.equals(email, that.email) &&
                Objects.equals(earningSearchParams, that.earningSearchParams);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, nickname, password, email, earningSearchParams);
    }
}
