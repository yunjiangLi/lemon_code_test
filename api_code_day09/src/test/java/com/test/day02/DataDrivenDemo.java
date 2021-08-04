package com.test.day02;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

public class DataDrivenDemo {

    @Test(dataProvider = "getLoginDatas02")
    public void testLogin(ExcelPojo excelPojo){
        //RestAssured全局配置
        //json小数返回类型是BigDecimal
        RestAssured.config=RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //BaseUrl全局配置
        RestAssured.baseURI="http://api.lemonban.com/futureloan";
        //接口入参
        //String json = "{\"mobile_phone\":\"13323231333\",\"pwd\":\"12345678\"}";
        String inputParams = excelPojo.getInputParams();
        //接口地址
        String url = excelPojo.getUrl();
        //请求头
        String requestHeader = excelPojo.getRequestHeader();
        //把请求头转成map
        Map requestHeaderMap = (Map) JSON.parse(requestHeader);
        //期望的响应结果
        String expected = excelPojo.getExpected();
        //把响应结果转成map
        Map<String,Object> expectedMap = (Map) JSON.parse(expected);
        Response res =
                given().
                        body(inputParams).
                        headers(requestHeaderMap).
                when().
                        post(url).
                then()
                        .log().all()
                        .extract().response();
        //断言？？？
        //读取响应map里面的每一个key
        //作业：完成响应断言
        //思路：
        //1、循环变量响应map，取到里面每一个key（实际上就是我们设计的jsonPath表达式）
        //2、通过res.jsonPath.get(key)取到实际的结果，再跟期望的结果做对比（key对应的value）
        for (String key :expectedMap.keySet()){
            //获取map里面的value
            //获取期望结果
            Object expectedValue = expectedMap.get(key);
            //获取接口返回的实际结果（jsonPath表达式）
            Object actualValue =  res.jsonPath().get(key);
            Assert.assertEquals(actualValue,expectedValue);
        }

    }

    @DataProvider
    public Object[][] getLoginDatas01(){
        Object[][] datas = {{"13323230000","123456"},
                {"1332323111","123456"},
                {"13323230000","12345678"}};
        return datas;
    }

    @DataProvider
    public Object[] getLoginDatas02(){
        File file = new File("D:\\svn_ppt\\api_testcases_futureloan_v2.xls");
        //导入的参数对象
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(1);
        //读取Excel
        List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
        //把集合转换为一个一维数组
        return listDatas.toArray();
    }

    public static void main(String[] args) {
        File file = new File("src\\test\\resources\\api_testcases_futureloan_v2.xls");
        //导入的参数对象
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(1);
        //读取Excel
        List<Object> listDatas = ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
        for (Object object: listDatas){
            System.out.println(object);
        }
    }
}
