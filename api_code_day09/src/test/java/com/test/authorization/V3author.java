package com.test.authorization;

import com.lemon.encryption.RSAManager;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class V3author {
    //全局变量
    int memberId;
    String token;

    @Test()
    public void testLogin(){
        String json = "{\"mobile_phone\":\"13300001333\",\"pwd\":\"12345678\"}";
        Response res =
                given().
                        log().all().
                        body(json).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v3").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then()
                        .log().all()
                        .extract().response();
        //1、先来获取id
        memberId = res.jsonPath().get("data.id");
        System.out.println(res.jsonPath().get("data.id")+"");
        //2、获取token
        token = res.jsonPath().get("data.token_info.token");
        System.out.println(token);
    }

    @Test(dependsOnMethods = "testLogin")
    public void testRecharge() throws Exception {
        //timestamp参数
        long timestamp = System.currentTimeMillis()/1000;
        //sign参数
        //1、取token的前面50位
        String preStr = token.substring(0,50);
        //2、取到的结果拼接上timestamp
        String str = preStr+timestamp;
        //3、通过RSA加密算法对拼接的结果进行加密,得到sign签名
        String sign = RSAManager.encryptWithBase64(str);
        //发起“充值”接口请求
        String jsonData = "{\"member_id\":"+memberId+",\"amount\":100000.00,\"timestamp\":\""+timestamp+"\",\"sign\":\""+sign+"\"}";
        Response res2 =
                given().
                        body(jsonData).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v3").
                        header("Authorization","Bearer "+token).
                when().
                        post("http://api.lemonban.com/futureloan/member/recharge").
                then().
                        log().all().extract().response();
        System.out.println("当前可用余额:"+res2.jsonPath().get("data.leave_amount"));
    }

    //public static void main(String[] args) throws Exception {
        //毫秒级别的时间戳
        //System.out.println(System.currentTimeMillis());
        //秒级别的时间戳
        //System.out.println(System.currentTimeMillis()/1000);
        //String token ="eyJhbGciOiJIUzUxMiJ9.eyJtZW1iZXJfaWQiOjEsImV4cCI6MTU2NzM0MzE0NH0.gc_U13I8fr-6K3x3MFAUD_Kc 8xXzgB8qg8MG4FGV1cifTchhFGGZu0E3sZ0pBCHgYizLbt4TIrNJSyhnz5uEcQ";
        //String result = token.substring(0,50);
        //System.out.println(result);
        //String result = RSAManager.encryptWithBase64("eyJhbGciOiJIUzUxMiJ9.eyJtZW");
        //System.out.println(result);

   // }
}
