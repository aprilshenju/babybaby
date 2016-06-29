package com.umeijia.wechat;

/**
 * Created by hadoop on 2016/6/29.
 */
public class EventMessage extends BaseMessage{
    private String event;
    private String eEventKey;


    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String geteEventKey() {
        return eEventKey;
    }

    public void seteEventKey(String eEventKey) {
        this.eEventKey = eEventKey;
    }

}
