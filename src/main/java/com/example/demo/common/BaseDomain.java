package com.example.demo.common;

/**
 * Created by dashuai on 2018/3/6.
 */
public class BaseDomain {
    private Integer page;  //当前页

    private Integer pageSize;  //页面显示数

    private String orderKey;  //排序

    public Integer getPage() {
        if(page == null){
            return 0;
        }else{
            return page;
        }
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        if(page == null){
            return 10;  //页面默认显示10
        }else{
            return pageSize;
        }
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }
}
