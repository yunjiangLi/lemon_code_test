package com.lemon.testcases;

import com.alibaba.fastjson.JSONObject;
import com.lemon.common.BaseTest;
import com.lemon.data.Environment;
import com.lemon.pojo.ExcelPojo;
import com.lemon.util.PhoneRandomUtil;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;

public class LoginTest extends BaseTest {

    @BeforeClass
    public void setup(){
        //生成一个没有被注册过的手机号码
        String phone = PhoneRandomUtil.getUnregisterPhone();
        Environment.envData.put("phone",phone);
        //前置条件
        //读取Excel里面的第一条数据->执行->生成一条注册过了手机号码
        List<ExcelPojo> listDatas = readSpecifyExcelData(2,0,1);
        ExcelPojo excelPojo = listDatas.get(0);
        //替换
        excelPojo = casesReplace(excelPojo);
        //执行【注册】接口请求
        Response res = request(excelPojo,"login");
        //提取注册返回的手机号码保存到环境变量中
        extractToEnvironment(excelPojo,res);
    }

    @Test(dataProvider = "getLoginDatas")
    public void testLogin(ExcelPojo excelPojo){
        //替换用例数据
        excelPojo = casesReplace(excelPojo);
        //发起登录请求
        Response res = request(excelPojo,"login");
        //断言
        assertResponse(excelPojo,res);
    }

    @DataProvider
    public Object[] getLoginDatas(){
        List<ExcelPojo> listDatas = readSpecifyExcelData(2,1);
        //把集合转换为一个一维数组
        return listDatas.toArray();
    }

}
