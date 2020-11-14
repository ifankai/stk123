package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_USER")
public class StkUser implements Serializable {

    @Column(name="ID", pk=true)
    private Integer id;

    @Column(name="NICKNAME")
    private String nickname;

    @Column(name="PASSWORD")
    private String password;

    @Column(name="EMAIL")
    private String email;

    @Column(name="EARNING_SEARCH_PARAMS")
    private String earningSearchParams;


    public Integer getId(){
        return this.id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getNickname(){
        return this.nickname;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getPassword(){
        return this.password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public String getEarningSearchParams(){
        return this.earningSearchParams;
    }
    public void setEarningSearchParams(String earningSearchParams){
        this.earningSearchParams = earningSearchParams;
    }


    public String toString(){
        return "id="+id+",nickname="+nickname+",password="+password+",email="+email+",earningSearchParams="+earningSearchParams;
    }

}
