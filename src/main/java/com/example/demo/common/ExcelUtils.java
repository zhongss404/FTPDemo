package com.example.demo.common;

import com.example.demo.config.ExcelConfigProps;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by dashuai on 2017/9/29.
 */
@Component
public class ExcelUtils {
    @Autowired
    private ExcelConfigProps excelConfigProps;

    /**
     * 根据上传的Excel类型【03、07】生成对应的工作workbook
     * @param multipartFile
     * @return
     * @throws Exception
     */
    public Workbook getWorkbook(MultipartFile multipartFile) throws Exception{
        Workbook workbook;
        if(multipartFile.getOriginalFilename().endsWith(".xls")){   //03版Excel
            workbook = new HSSFWorkbook(multipartFile.getInputStream());
        }else{   //07版Excel
            workbook = new XSSFWorkbook(multipartFile.getInputStream());
        }
        return workbook;
    }

    /**
     * 将Excel文件转化为特殊的String
     * @param rankNum  列数（从1开始计算）
     * @param workbook
     * @return
     * @throws Exception
     */
    public List<String[]> parseExcel(Integer rankNum,Workbook workbook) throws Exception{
        List<String[]> parseResult = new ArrayList<>();
        for(int i=0;i<workbook.getNumberOfSheets();i++){  //获取所有的sheet
            Sheet sheet = workbook.getSheetAt(i);
            for(int j=0;j<sheet.getLastRowNum()+1;j++){  //从第一行开始读取（包括标题）
                Row row = sheet.getRow(j);
                if(row != null){
                    String[] cells = new String[rankNum];
                    int nullNumber = 0;
                    for(int k=0;k<rankNum;k++){ //列
                        StringBuffer sbuf = new StringBuffer(cellToString(row.getCell(k)));
                        if(sbuf.toString().isEmpty()){
                            nullNumber++;
                            continue;
                        }
                        cells[k] = sbuf.toString();
                    }
                    if(nullNumber != rankNum){  //避免空行影响后续的操作
                        parseResult.add(cells);
                    }
                }
            }
        }
        return parseResult;
    }

    /**
     * 将“封装数据的字符串数组”转为“Excel文件”
     * @param actionType 操作类型(upload/download)
     * @param filePrefix 文件前缀
     * @param catalog 保存位置
     * @param parseResult 字符串数组
     * @throws Exception
     */
    public void generateExcel(String actionType,String filePrefix,String catalog,List<String[]> parseResult) throws Exception{
        HSSFWorkbook workbook;
        HSSFSheet sheet;
        if("download".equals(actionType)){   //下载
            workbook = new HSSFWorkbook(new FileInputStream(new File(excelConfigProps.getTemplateLocation() + "/" + filePrefix + ".xls")));
            sheet = workbook.getSheetAt(0);
        }else{                               //上传
            generateTemplate(filePrefix,parseResult.get(0));  //生成一个模版(包含标题)
            parseResult.remove(0);  //删除标题那一列在parseResult中的数据
            workbook = new HSSFWorkbook();
            sheet = workbook.createSheet();
        }
        for(int rowNum = 0;rowNum < parseResult.size();rowNum ++){
            HSSFRow row = sheet.createRow(rowNum+1);   //从第二行开始生成;
            for(int cellNum = 0;cellNum < parseResult.get(rowNum).length;cellNum++){
                Cell cell = row.createCell(cellNum);
                cell.setCellValue(parseResult.get(rowNum)[cellNum]);
            }
        }
        File filePath = new File(catalog);
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        File file = new File(catalog + filePrefix + "-" + new SimpleDateFormat("HHmmss").format(new Date()) + ".xls");
        file.createNewFile();
        workbook.write(new FileOutputStream(file));
        workbook.close();
    }

    /**
     * 获取最近一次的上传的文件
     * @Param catalog upload/download目录
     * @return
     */
    public File getRecentlyFile(String catalog){
        File[] fileList = new File(catalog).listFiles();
        if(fileList.length > 0){
            File theLast = fileList[0];
            for(int i=1;i<fileList.length;i++){
                if(fileList[i].lastModified() > theLast.lastModified()){
                    theLast = fileList[i];
                }
            }
            return new File(catalog + theLast.getName());
        }
        return null;
    }

    /**
     * 当上传目录中excel文件超过一定值时，删除不是30分钟内的文件
     * @param setNum
     * @param file
     */
    public void deleteOldFiles(Integer setNum,File file){
        long MILLS = 10 * 60 * 1000;
        File[] files = file.listFiles();
        if(files.length > setNum){
            for(int i=0;i<files.length;i++){
                if(new Date().getTime() - files[i].lastModified() > MILLS){
                    files[i].delete();
                }
            }
        }
    }

    /**
     * 处理toString方法获得数据
     * @param objectToStringResult
     * @return
     */
    public String[] toStringResult2Array(String objectToStringResult){
        String[] var1 = objectToStringResult.replace("}","").trim().split(",");
        String[] var2 = new String[var1.length];
        for(int i=0;i<var1.length;i++){
            StringBuffer sbuf = new StringBuffer(var1[i].split("=")[1]);
            if("null".equals(sbuf.toString())){
                continue;
            }
            var2[i] = sbuf.toString();
        }
        return var2;
    }

    public String[] toStringResult2Array(String className,String objectToStringResult){
        String[] var1 = objectToStringResult.replaceAll(className + "\\{", "").replaceAll("\\}", "").trim().split(",");
        String[] var2 = new String[var1.length * 2];
        int j=0;
        for (int i = 0; i < var1.length; i++,j=j+2) {
            String[] var4 = var1[i].split("=");
            var2[j] = captureName(var4[0].trim());
            if ("null".equals(var4[1])) {
                continue;
            }
            var2[j + 1] = var4[1];
        }
        return var2;
    }

    public String[] getClassMethods(String packageName,String className) throws Exception{
        String toStringResult = Class.forName(packageName + "." + className).newInstance().toString();
        String[] var1 = toStringResult.replaceAll(className + "\\{", "").replaceAll("\\}", "").trim().split(",");
        String[] methodName = new String[var1.length];
        for(int i=0;i<var1.length;i++){
            methodName[i] = captureName(var1[i].split("=")[0].trim());
        }
        return methodName;
    }

    public <T> List<T> parse2Object(Class<T> clazz, String[] methods, List<String[]> data) throws Exception{
        List<T> result = new ArrayList<>();
        for(int i=0;i<data.size();i++){
            String[] values = data.get(i);
            T object = clazz.newInstance();
            Method method;
            for(int j=0;j<methods.length;j++){
                if(methods[j].contains("Date")){
                    method = clazz.getMethod("set" + methods[j],Date.class);
                    method.invoke(object,values[j] != null ? new SimpleDateFormat(getPattern(values[j])).parse(values[j]) :null);
                }else{
                    method = clazz.getMethod("set" + methods[j],String.class);
                    method.invoke(object,values[j]);
                }
            }
            result.add(object);
        }
        return result;
    }

    /**
     * 导出数据时设置请求头并将文件转为输出流
     * @param file
     * @param request
     * @param response
     * @throws Exception
     */
    public void setHeaderAndParseFile(File file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //1、设置response请求头
        response.setContentType("application/x-download;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        //2、将file文件转换为输出流
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
        byte[] buffer = new byte[2048];
        int readLength;
        while((readLength = bis.read(buffer,0,buffer.length)) != -1){
            bos.write(buffer,0,readLength);
        }
        bis.close();
        bos.close();
        file.delete();
    }

    public void generateImportReport(String content) throws Exception{
        File filePath = new File(excelConfigProps.getReportLocation());
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-","").trim().substring(0,10);
        File file = new File(excelConfigProps.getReportLocation() + "/未导入成功记录-" + uuid + ".txt");
        BufferedWriter ow = new BufferedWriter(new FileWriter(file));
        ow.write(content);
        ow.close();
    }

    /**
     * 对单元格的数据进行必要的转换
     * @param cell
     * @return
     */
    private String cellToString(Cell cell){
        if(cell != null){
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    return cell.getStringCellValue();
                case Cell.CELL_TYPE_BOOLEAN:
                    Boolean val1 = cell.getBooleanCellValue();
                    return val1.toString();
                case Cell.CELL_TYPE_NUMERIC:
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        Date theDate = cell.getDateCellValue();
                        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
                        return dff.format(theDate);
                    } else {
                        DecimalFormat df = new DecimalFormat("0");
                        return df.format(cell.getNumericCellValue());
                    }
            }
        }
        return "";
    }

    /**
     * 生成只有标题的excel
     * @param filePrefix
     * @param titles
     * @throws Exception
     */
    private void generateTemplate(String filePrefix,String[] titles) throws Exception{
        File filePath = new File(excelConfigProps.getTemplateLocation());
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        File file = new File(excelConfigProps.getTemplateLocation() + "/" + filePrefix + ".xls");
        if(!file.exists()){     //不存在时候新增
            file.createNewFile();
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            HSSFRow row = sheet.createRow(0);
            for(int i = 0;i < titles.length;i ++){
                Cell cell = row.createCell(i);
                cell.setCellValue(titles[i]);
            }
            workbook.write(new FileOutputStream(file));
            workbook.close();
        }
    }

    /**
     * 将字符串首字母转为大写
     * @param name
     * @return
     */
    private String captureName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    private String getPattern(String date){
        if(date.contains("/")){
            return "yyyy/MM/dd";
        }else if(date.contains("-")){
            return "yyyy-MM-dd";
        }else{
            return "yyyyMMdd";
        }
    }
}
