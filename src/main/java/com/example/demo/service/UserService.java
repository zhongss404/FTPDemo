package com.example.demo.service;

import com.example.demo.common.ExcelUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * Created by dashuai on 2018/3/6.
 */
@Service
public class UserService {

    @Autowired
    private ExcelUtils excelUtils;

    public void upload(String catalog,String filePrefix,Integer lines,MultipartFile multipartFile) throws Exception {
        Workbook workbook = excelUtils.getWorkbook(multipartFile);
        List<String[]> data = excelUtils.parseExcel(lines,workbook);
        excelUtils.generateExcel("upload",filePrefix,catalog,data);    //生成Excel
        //当上传目录内容超过5个时，删除10分钟外的内容
        excelUtils.deleteOldFiles(5,new File(catalog));
    }
}
