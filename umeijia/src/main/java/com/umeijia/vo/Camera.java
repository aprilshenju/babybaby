package com.umeijia.vo;


public class Camera {
    public Camera() {
    }

    public Camera(long id, String ip_url, String video_url, String description, String manufactory, Class cla, Kindergarten garten, String type, String state, String thumb_path, String active_period, boolean is_public) {
        this.id = id;
        this.ip_url = ip_url;
        this.video_url = video_url;
        this.description = description;
        this.manufactory = manufactory;
        this.cla = cla;
        this.garten = garten;
        this.type = type;
        this.state = state;
        this.thumb_path = thumb_path;
        this.active_period = active_period;
        this.is_public = is_public;
    }

    public Camera(String ip_url, String video_url, String description, String manufactory, Class cla, Kindergarten garten, String type, String state, String thumb_path, String active_period, boolean is_public) {
        this.ip_url = ip_url;
        this.video_url = video_url;
        this.description = description;
        this.manufactory = manufactory;
        this.cla = cla;
        this.garten = garten;
        this.type = type;
        this.state = state;
        this.thumb_path = thumb_path;
        this.active_period = active_period;
        this.is_public = is_public;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp_url() {
        return ip_url;
    }

    public void setIp_url(String ip_url) {
        this.ip_url = ip_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufactory() {
        return manufactory;
    }

    public void setManufactory(String manufactory) {
        this.manufactory = manufactory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getThumb_path() {
        return thumb_path;
    }

    public void setThumb_path(String thumb_path) {
        this.thumb_path = thumb_path;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

//    public boolean isIs_public() {
//        return is_public;
//    }
//
//    public void setIs_public(boolean is_public) {
//        this.is_public = is_public;
//    }


    private long id;
    private String ip_url;
    private String video_url;


    public void setGarten(Kindergarten garten) {
        this.garten = garten;
    }

    public Class getCla() {
        return cla;
    }

    public void setCla(Class cla) {
        this.cla = cla;

    }

    public Kindergarten getGarten() {

        return garten;
    }

    private String description;
    private String manufactory;
    private Class cla;  //公公摄像头 classid为 -1
    private Kindergarten garten;

    private String type;  //班级私有还是 公开摄像头

    private String state;
    private String thumb_path; //缩略图路径

    public String getActive_period() {
        return active_period;
    }

    public void setActive_period(String active_period) {
        this.active_period = active_period;
    }

    private String active_period;

    public boolean is_public() {
        return is_public;
    }

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }

    private boolean is_public; // 是否是公共摄像头

    /**
     * 根据时间段判断，是否在线开放
     **/
    public boolean isActive() {

        return true;
    }

}
