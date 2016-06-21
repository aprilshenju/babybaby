package com.umeijia.vo;


import java.util.Date;

public class Camera {
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
	public boolean isIs_public() {
		return is_public;
	}
	public void setIs_public(boolean is_public) {
		this.is_public = is_public;
	}

	
	private long id;
	private String ip_url;
	private String video_url;

	public boolean is_public() {
		return is_public;
	}

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
	private boolean is_public; // 是否是公共摄像头 

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public void setModified_date(Date modified_date) {
		this.modified_date = modified_date;
	}

	public Date getModified_date() {
		return modified_date;

	}

	private Date create_date; //摄像头创建 日期
	private Date modified_date; //修改日期


	/**
	 * 根据时间段判断，是否在线开放
	 * **/
	public boolean isActive(){

			return  true;
	}

}
