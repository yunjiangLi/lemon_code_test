package com.lemon.util;

import java.util.Random;

public class PhoneRandomUtil {
    public static void main(String[] args) {
        //思路一、先查询手机号码字段，按照倒叙排列，取得最大的手机号码+1
        //思路二、先去生成一个随机的手机号码，再通过该号码进入到数据库查询，如果查询有记录，再来生成一个，否则说明该号码没有被注册（循环）
        System.out.println(getUnregisterPhone());
    }

    public static String getRandomPhone(){
        Random random = new Random();
        //nextInt随机生成一个整数，范围是从0-你的参数范围之内 13323234545
        String phonePrefix="133";
        //生成8位随机整数-循环拼接
        for(int i=0 ;i<8 ; i++){
            //生成一个0-9的随机整数
            int num = random.nextInt(9);
            phonePrefix = phonePrefix+num;
        }
        return phonePrefix;
    }

    public static String getUnregisterPhone(){
        String phone = "";
        while(true){
            phone = getRandomPhone();
            //查询数据
            Object result = JDBCUtils.querySingleData("select count(*) from member where mobile_phone="+phone);
            if((Long)result == 0){
                //表示没有被注册，符合需求
                break;
            }else {
                //表示已经被注册了，还需继续执行上述过程
                continue;
            }
        }
        return phone;
    }
}
