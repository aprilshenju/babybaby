package com.umeijia.wechat;

/**
 * Created by hadoop on 2016/6/29.
 */
public class TextMessage extends  BaseMessage{
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        this.Content = content;
    }
}
