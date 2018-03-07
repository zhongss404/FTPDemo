package com.example.demo.service;

import com.example.demo.common.JsonResult;
import com.example.demo.dao.domain.User;
import com.example.demo.dao.domain.UserExample;
import com.example.demo.dao.dto.UserDto;
import com.example.demo.dao.mapper.UserMapper;
import com.example.demo.dao.mapper.UserMapperExt;
import com.example.demo.utils.ExcelUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by dashuai on 2018/3/6.
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserMapperExt userMapperExt;

    @Autowired
    private ExcelUtils excelUtils;

    /**
     * 查询
     * @param userDto
     * @return
     */
    public JsonResult query(UserDto userDto){
        JsonResult result = new JsonResult();
        //分页不可用
        PageInfo data = PageHelper.startPage(userDto.getPage(),userDto.getPageSize()).doSelectPageInfo(()->userMapperExt.selectByCondition(userDto));
        result.setData(userMapperExt.selectByCondition(userDto));
        return result;
    }

    /**
     * 上传文件
     * @param catalog
     * @param filePrefix
     * @param lines
     * @param multipartFile
     * @throws Exception
     */
    public void upload(String catalog,String filePrefix,Integer lines,MultipartFile multipartFile) throws Exception {
        Workbook workbook = excelUtils.getWorkbook(multipartFile);
        List<String[]> data = excelUtils.parseExcel(lines,workbook);
        excelUtils.generateExcel("upload",filePrefix,catalog,data);    //生成Excel
        //当上传目录内容超过5个时，删除10分钟外的内容
        excelUtils.deleteOldFiles(5,new File(catalog));
    }

    /**
     * 导入文件
     * @param catalog
     * @param lines
     * @return
     * @throws Exception
     */
    public JsonResult importUser(String catalog,Integer lines) throws Exception{
        File file = excelUtils.getRecentlyFile(catalog);
        if(file == null){
            return new JsonResult("清除上传列表中的文件后,请重新上传文件!");
        }
        String[] methodLists = excelUtils.getClassMethods("com.example.demo.dao.domain","User");
        List<String[]> data = excelUtils.parseExcel(lines,new HSSFWorkbook(new FileInputStream(file)));
        List<User> users = excelUtils.parse2Object(User.class,methodLists,data);
        return batchHandle(users);
    }

    /**
     * 导出文件
     * @param catalog
     * @param filePrefix
     * @param request
     * @param response
     * @throws Exception
     */
    public void exportUser(String catalog, String filePrefix, HttpServletRequest request, HttpServletResponse response) throws Exception{
        //1、提前生成需要导出的excel文件
        excelUtils.generateExcel("download",filePrefix,catalog,generateDataSource());
        File file = excelUtils.getRecentlyFile(catalog);
        //2、设置response请求头并将file文件转换为输出流
        excelUtils.setHeaderAndParseFile(file,request,response);
    }

    /**
     * 导入时批量操作数据（更新/新增）
     * @param users
     * @return
     * @throws Exception
     */
    private JsonResult batchHandle(List<User> users) throws Exception{
        StringBuffer sbuf = new StringBuffer();
        for(int i=0;i<users.size();i++){
            User user = users.get(i);
            if(user != null){
                String checkResult = checkProperties(user);
                if(checkResult != null){
                    sbuf.append("\r\nExcel文件第 " + (i+2) + " 行数据导入失败! 失败原因: " + checkResult)
                            .append("\r\n");
                    continue;
                }
                try{
                    List<User> validateResult = recordIsUnique(user.getCode());
                    if(validateResult.size() ==0){  //不存在，新增
                        user.setId(UUID.randomUUID().toString().replaceAll("-","").trim());
                        userMapper.insertSelective(user);
                    }else{                   //存在，更新
//                        user.setId(validateResult.get(0).getId());
                        UserExample userExample = new UserExample();
                        userExample.createCriteria().andIdEqualTo(validateResult.get(0).getId());
                        userMapper.updateByExampleSelective(user,userExample);
                    }
                }catch(Exception e){
                    sbuf.append("\r\nExcel文件第 " + (i+2) + " 行数据导入时出现异常! 异常信息:\r\n").append(e.toString()).append("\r\n");
                    continue;
                }
            }
        }
        if(sbuf.toString().length() > 0){
            //返回未成功的记录的id
            return new JsonResult(excelUtils.generateImportReport(sbuf.toString()));
        }
        return new JsonResult();
    }

    /**
     * 校验必须参数是否为空（自己根据情况自行添加）
     * @param user
     * @return
     */
    private String checkProperties(User user){
        if(user == null){
            return "请求错误";
        }
        if(user.getCode() == null){
            return "员工编号为空";
        }
        if(user.getUsername() == null){
            return "用户名为空";
        }
        if(user.getRealname() == null){
            return "姓名为空";
        }
        return null;
    }

    /**
     * 在本例子中根据“员工编号”判断用户是否存在；
     * 如果用户存在，后续操作是更新，否则为新增
     * @param code
     * @return
     */
    private List<User> recordIsUnique(String code){
        UserExample example = new UserExample();
        example.createCriteria().andCodeEqualTo(code);
        return userMapper.selectByExample(example);
    }

    /**
     * 下载时生成一定格式的数据
     * @return
     */
    private List<String[]> generateDataSource(){
        List<String[]> dataSource = new ArrayList<>();
        for(User user : userMapper.selectByExample(new UserExample())){
            dataSource.add(excelUtils.toStringResult2Array(user.toString()));
        }
        return dataSource;
    }
}
