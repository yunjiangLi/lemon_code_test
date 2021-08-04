package com.test.day01;

import io.restassured.RestAssured;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

public class RestAssuredDemo {
    @Test
    public void firstGetRequest(){
        given().
                //设置请求：请求头、请求体...
        when().
                get("https://www.baidu.com").
        then().
                log().body();
    }

    @Test
    public void getDemo01(){
        given().
                queryParam("mobilephone","13323234545").
                queryParam("pwd","123456").
        when().
                get("http://www.httpbin.org/get").
        then().
                log().body();
    }

    @Test
    public void postDemo01(){
        given().
                formParam("mobilephone","13323234545").
                formParam("pwd","123456").
                contentType("application/x-www-form-urlencoded").
        when().
                post("http://www.httpbin.org/post").
        then().
                log().body();
    }

    @Test
    public void postDemo02(){
        String jsonData="{\"mobilephone\":\"13323234545\",\"pwd\":\"123456\"}";
        given().
                body(jsonData).
                contentType("application/json").
        when().
                post("http://www.httpbin.org/post").
        then().
                log().body();
    }

    @Test
    public void postDemo03(){
        String xmlData="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<suite>\n" +
                "    <class>测试xml</class>\n" +
                "</suite>";
        given().
                body(xmlData).
                contentType("application/xml").
        when().
                post("http://www.httpbin.org/post").
        then().
                log().body();
    }

    @Test
    public void postDemo04(){
        given().
                multiPart(new File("D:\\KuGou\\text.txt")).
        when().
                post("http://www.httpbin.org/post").
        then().
                log().body();
    }
}
