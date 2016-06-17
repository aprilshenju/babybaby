package com.umeijia.vo;

import java.util.Date;

/**
 * Created by shenju on 2016/6/16.
 */
public class FeedBack {
    public int getRead_or_not() {
        return read_or_not;
    }

    public long getId() {
        return id;
    }

    public int getUser_type() {
        return user_type;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }

    public String getResponse() {
        return response;
    }

    public void setRead_or_not(int read_or_not) {
        this.read_or_not = read_or_not;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    private  long id;
    private  int user_type;
    private  long user_id;
    private  String content;
    private Date date;
    private  String response;
    private  int read_or_not;

}
