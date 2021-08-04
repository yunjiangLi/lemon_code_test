package com.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetResponse {
    @Test
    public void getResponseHeader(){
        Response res =
            given().
            when().
                    post("http://www.httpbin.org/post").
            then().
                    log().all().extract().response();
        System.out.println("接口的响应时间:"+res.time());
        System.out.println(res.getHeader("Content-Type"));
    }

    @Test
    public void getResponseJson01(){
        String json = "{\"mobile_phone\":\"13323231111\",\"pwd\":\"12345678\"}";
        Response res =
                given().
                        body(json).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        log().all().extract().response();
        System.out.println(res.jsonPath().get("data.id")+"");
    }

    @Test
    public void getResponseJson02(){
        Response res =
                given().
                when().
                        get("http://www.httpbin.org/json").
                then().
                        log().all().extract().response();
        System.out.println(res.jsonPath().get("slideshow.slides.title")+"");
        List<String> list = res.jsonPath().getList("slideshow.slides.title");
        System.out.println(list.get(0));
        System.out.println(list.get(1));
    }

    @Test
    public void getResponseHtml(){
        Response res =
                given().
                when().
                        get("http://www.baidu.com").
                then().
                        log().all().extract().response();
        System.out.println(res.htmlPath().get("html.head.meta[0].@http-equiv")+"");
        System.out.println(res.htmlPath().get("html.head.meta[0].@content")+"");
        System.out.println(res.htmlPath().getList("html.head.meta"));
    }

    @Test
    public void getResponseXml(){
        Response res =
                given().
                when().
                        get("http://www.httpbin.org/xml").
                then().
                        log().all().extract().response();
        System.out.println(res.xmlPath().get("slideshow.slide[1].title")+"");
        System.out.println(res.xmlPath().get("slideshow.slide[1].@type")+"");
    }

    @Test
    public void loginRecharge(){
        String json = "{\"mobile_phone\":\"13323231111\",\"pwd\":\"12345678\"}";
        Response res =
                given().
                        body(json).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then()
                        .extract().response();
        //1、先来获取id
        int memberId = res.jsonPath().get("data.id");
        System.out.println(memberId);
        //2、获取token
        String token = res.jsonPath().get("data.token_info.token");
        System.out.println(token);

        //发起“充值”接口请求
        String jsonData = "{\"member_id\":"+memberId+",\"amount\":100000.00}";
        Response res2 =
                given().
                        body(jsonData).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        header("Authorization","Bearer "+token).
                when().
                        post("http://api.lemonban.com/futureloan/member/recharge").
                then().
                        log().all().extract().response();
        System.out.println("当前可用余额:"+res2.jsonPath().get("data.leave_amount"));
    }

}
