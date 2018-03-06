package com.example.demo.dao.dto;

import com.example.demo.common.BaseDomain;

/**
 * Created by dashuai on 2018/3/6.
 */
public class UserDto extends BaseDomain {

    private String username;

    private String realname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }
}
