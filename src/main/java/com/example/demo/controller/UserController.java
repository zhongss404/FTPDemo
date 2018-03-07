package com.example.demo.controller;

import com.example.demo.common.JsonResult;
import com.example.demo.config.ExcelConfigProps;
import com.example.demo.dao.dto.UserDto;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @RequestMapping(value = "/query",method = RequestMethod.GET)
    public JsonResult query(UserDto userDto){
        return userService.query(userDto);
    }

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

    @RequestMapping(value = "/import")
    public JsonResult importUser() throws Exception{
        return userService.importUser(excelConfigBean.getUploadLocation() + PATHSUFFIX,LINES);
    }

    @RequestMapping(value = "/export")
    public void exportUser(HttpServletRequest request, HttpServletResponse response){
        try {
            userService.exportUser(excelConfigBean.getDownloadLocation() + PATHSUFFIX,FILEPREFIX,request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
