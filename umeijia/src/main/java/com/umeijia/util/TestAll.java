package com.umeijia.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shenju on 2016/6/21.
 */
public class TestAll {

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
     //   try {
       /*     Date date = sdf.parse("2016-07-03");
            System.out.print(date.toString());*/



    /*   } catch (ParseException e) {
            e.printStackTrace();
        }*/

        Date d = new Date();
        System.out.println(d.toString());
    }
}
