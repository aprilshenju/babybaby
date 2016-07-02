package com.umeijia.wechat;

/**
 * Created by hadoop on 2016/6/29.
 */
public class EventMessage extends BaseMessage{
    private String Event;
    private String EventKey;


    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        this.Event = event;
    }

    public String geteEventKey() {
        return EventKey;
    }

    public void seteEventKey(String eEventKey) {
        this.EventKey = eEventKey;
    }

}
