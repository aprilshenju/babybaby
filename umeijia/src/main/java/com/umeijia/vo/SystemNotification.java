package com.umeijia.vo;

import java.util.Date;

/**
 * Created by dolphin0520 on 16-7-2.
 */
public class SystemNotification {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private long id;
    private String title;
    private String content;
    private Date date;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    private boolean valid;

    public SystemNotification(){
        this.valid=true;
    }



}
