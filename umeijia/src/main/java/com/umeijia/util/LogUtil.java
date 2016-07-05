package com.umeijia.util;

import com.umeijia.vo.DailyLog;

import java.util.Date;

/**
 * Created by hadoop on 2016/7/5.
 */
public class LogUtil {
    public static DailyLog generateDailyLog(Date log_date,
            int user_type,
            long user_id,
            String op_type,
           String op_content,
            String op_object) {

        DailyLog dailyLog = new DailyLog();
        dailyLog.setLog_date(log_date);
        dailyLog.setUser_type(user_type);
        dailyLog.setUser_id(user_id);
        dailyLog.setOp_type(op_type);
        dailyLog.setOp_content(op_content);
        dailyLog.setOp_object(op_object);
        return dailyLog;
    }
}
