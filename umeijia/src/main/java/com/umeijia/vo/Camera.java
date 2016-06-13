package com.umeijia.vo;



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
	public long getClass_id() {
		return class_id;
	}
	public void setClass_id(long class_id) {
		this.class_id = class_id;
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
	public String getBegin() {
		return begin;
	}
	public void setBegin(String begin) {
		this.begin = begin;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
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
	public long getGarten_id() {
		return garten_id;
	}
	public void setGarten_id(long garten_id) {
		this.garten_id = garten_id;
	}
	
	private long id;
	private String ip_url;
	private String video_url;
	private String description;
	private String manufactory;
	private long class_id;
	private long garten_id;

	private String type;
	private String state;
	private String thumb_path; //缩略图路径
	private String begin,end;
	private boolean is_public; // 是否是公共摄像头 
	
}
