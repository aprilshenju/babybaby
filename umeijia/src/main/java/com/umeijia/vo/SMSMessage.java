package com.umeijia.vo;

import java.util.Date;

/**
 * Created by dolphin0520 on 16-6-27.
 */
public class SMSMessage {
    public SMSMessage(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Date getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(Date lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    private String phoneNum;
    private long id;
    private String verifyCode;
    private Date validTimeDeadLine;
    private Date lastRequestTime; //上次请求时间

    public Date getLastInputVerifyCodeTime() {
        return lastInputVerifyCodeTime;
    }

    public void setLastInputVerifyCodeTime(Date lastInputVerifyCodeTime) {
        this.lastInputVerifyCodeTime = lastInputVerifyCodeTime;
    }

    private Date lastInputVerifyCodeTime; //上次输入验证码的时间
    private String unUsedOne;
    private String unUsedTwo;

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }



    public Date getValidTimeDeadLine() {
        return validTimeDeadLine;
    }

    public void setValidTimeDeadLine(Date validTimeDeadLine) {
        this.validTimeDeadLine = validTimeDeadLine;
    }



    public String getUnUsedOne() {
        return unUsedOne;
    }

    public void setUnUsedOne(String unUsedOne) {
        this.unUsedOne = unUsedOne;
    }



    public String getUnUsedTwo() {
        return unUsedTwo;
    }

    public void setUnUsedTwo(String unUsedTwo) {
        this.unUsedTwo = unUsedTwo;
    }


}
