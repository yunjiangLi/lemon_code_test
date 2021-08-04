package com.lemon.testcases;

import com.alibaba.fastjson.JSONObject;
import com.lemon.common.BaseTest;
import com.lemon.data.Environment;
import com.lemon.encryption.RSAManager;
import com.lemon.pojo.ExcelPojo;
import com.lemon.util.PhoneRandomUtil;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;

public class RechargeTest extends BaseTest {

    @BeforeClass
    public void setup() {
        //生成一个没有被注册过的手机号码
        String phone = PhoneRandomUtil.getUnregisterPhone();
        //保存到环境变量中
        Environment.envData.put("phone",phone);
        //读取用例前置条件数据
        List<ExcelPojo> listDatas = readSpecifyExcelData(3, 0, 2);
        //注册
        ExcelPojo registerExcelPojo = listDatas.get(0);
        //参数替换
        registerExcelPojo = casesReplace(registerExcelPojo);
        //发起接口请求-注册
        Response registerRes = request(registerExcelPojo,"recharge");
        //提取接口返回对应的字段保存到环境变量中
        extractToEnvironment(registerExcelPojo,registerRes);
        //登录
        ExcelPojo loginExcelPojo =listDatas.get(1);
        //参数替换，替换{{phone}}
        loginExcelPojo = casesReplace(loginExcelPojo);
        //发起接口请求-登录
        Response resLogin = request(loginExcelPojo,"recharge");
        //提取接口返回对应的字段保存到环境变量中
        extractToEnvironment(loginExcelPojo,resLogin);
        System.out.println("token::"+Environment.envData.get("token"));
    }

    @Test(dataProvider = "getRechargeDatas")
    public void testRecharge(ExcelPojo excelPojo) throws Exception {
        //timestamp参数
        long timestamp = System.currentTimeMillis()/1000;
        //sign参数
        //1、取token的前面50位
        String token = (String) Environment.envData.get("token");
        String preStr = token.substring(0,50);
        //2、取到的结果拼接上timestamp
        String str = preStr+timestamp;
        //3、通过RSA加密算法对拼接的结果进行加密,得到sign签名
        String sign = RSAManager.encryptWithBase64(str);
        //保存到环境变量中
        Environment.envData.put("timestamp",timestamp);
        Environment.envData.put("sign",sign);

        excelPojo = casesReplace(excelPojo);
        Response res = request(excelPojo,"recharge");
        //断言
        assertResponse(excelPojo,res);
    }

    @DataProvider
    public Object[] getRechargeDatas() {
        List<ExcelPojo> listDatas = readSpecifyExcelData(3, 2);
        return listDatas.toArray();
    }
}
