package com.example.demo.controller;

import com.example.demo.config.ExcelConfigProps;
import com.example.demo.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * Created by dashuai on 2018/1/31.
 */
@RestController
@RequestMapping(value = "/gateway")
public class ExportReportController {

    @Autowired
    private ExcelUtils excelUtils;
    @Autowired
    private ExcelConfigProps excelConfigProps;

    @RequestMapping(value = "/download_report")
    public void downloadReport(HttpServletRequest request, HttpServletResponse response){
        File file = new File(excelConfigProps.getReportLocation() + "/未导入成功记录-" + request.getParameter("uuid") + ".txt");
        try {
            excelUtils.setHeaderAndParseFile(file,request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
