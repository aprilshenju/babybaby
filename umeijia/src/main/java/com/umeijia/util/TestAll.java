package com.umeijia.util;

import java.text.SimpleDateFormat;

/**
 * Created by shenju on 2016/6/21.
 */
public class TestAll {

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

        Long e1=new Long(3);
        Long e2=new Long(3);
        if(e1==3){
            System.out.print("equal....");
        }


     //   try {
       /*     Date date = sdf.parse("2016-07-03");
            System.out.print(date.toString());*/



    /*   } catch (ParseException e) {
            e.printStackTrace();
        }*/

    System.out.print(MD5.GetSaltMD5Code("agent123"));
    }
}
