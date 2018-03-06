package com.example.demo.domain;

import java.util.Date;

/**
 * Created by dashuai on 2018/3/6.
 */
public class User {

    private String code;        //员工编号

    private String username;    //用户名

    private String realname;    //姓名

    private String age;         //年龄

    private Date empdate;       //入职时间

    private String address;     //家庭地址

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Date getEmpdate() {
        return empdate;
    }

    public void setEmpdate(Date empdate) {
        this.empdate = empdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("username", username)
                .add("realname", realname)
                .add("age", age)
                .add("empdate", empdate)
                .add("address", address)
                .toString();
    }
}
