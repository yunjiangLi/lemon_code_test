package com.test.day02;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

public class AssertDemo {
    int memberId;
    String token;

    @Test
    public void testLogin(){
        //RestAssured全局配置
        //json小数返回类型是BigDecimal
        RestAssured.config=RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //BaseUrl全局配置
        RestAssured.baseURI="http://api.lemonban.com/futureloan";


        String json = "{\"mobile_phone\":\"13323231333\",\"pwd\":\"12345678\"}";
        Response res =
                given().
                        body(json).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                when().
                        post("/member/login").
                then()
                        .log().all()
                        .extract().response();
        //1、响应结果断言
        //整数类型
        int code = res.jsonPath().get("code");
        //字符串类型
        String msg = res.jsonPath().get("msg");
        Assert.assertEquals(code,0);
        Assert.assertEquals(msg,"OK");
        //小数类型
        //注意：restassured里面如果返回json小数，那么其类型是float
        //丢失精度问题解决方案：声明restassured返回json小数的其类型是BigDecimal
        BigDecimal actual = res.jsonPath().get("data.leave_amount");
        BigDecimal expected = BigDecimal.valueOf(50000.01);
        Assert.assertEquals(actual,expected);
        //java.lang.AssertionError: expected [10000.01] but found [10000.01]
        //因为类型不匹配
        //2、数据库断言
        memberId = res.jsonPath().get("data.id");
        token = res.jsonPath().get("data.token_info.token");
    }

    @Test(dependsOnMethods = "testLogin")
    public void testRecharge(){
        //发起“充值”接口请求
        String jsonData = "{\"member_id\":"+memberId+",\"amount\":10000.00}";
        Response res2 =
                given().
                        body(jsonData).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        header("Authorization","Bearer "+token).
                when().
                        post("/member/recharge").
                then().
                        log().all().extract().response();
        BigDecimal actual2 = res2.jsonPath().get("data.leave_amount");
        BigDecimal expected2 = BigDecimal.valueOf(60000.01);
        Assert.assertEquals(actual2,expected2);
    }
}
