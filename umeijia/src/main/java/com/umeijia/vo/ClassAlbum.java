package com.umeijia.vo;

import java.util.Date;

/**
 * 班级相册 
 * **/
public class ClassAlbum {
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getClass_id() {
		return class_id;
	}
	public void setClass_id(long class_id) {
		this.class_id = class_id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getImage_name() {
		return image_name;
	}
	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}
	private long id;
	private long class_id;
	private Date date; 
	private String image_name;
	
}
