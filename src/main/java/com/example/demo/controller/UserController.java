package com.example.demo.controller;

import com.example.demo.config.ExcelConfigProps;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by dashuai on 2018/3/6.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ExcelConfigProps excelConfigBean;

    //Excel列总数
    private static final Integer LINES = 6;

    //路径后缀
    private static final String PATHSUFFIX = "/";

    //文件前缀
    private static final String FILEPREFIX = "user";

    @RequestMapping(value = "/upload")
    public void upload(MultipartFile multipartFile){
        if(multipartFile != null){
            try {
                userService.upload(excelConfigBean.getUploadLocation() + PATHSUFFIX,FILEPREFIX,LINES,multipartFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}