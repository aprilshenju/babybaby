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
	public String getImage_names() {
		return image_names;
	}
	public void setImage_names(String image_names) {
		this.image_names = image_names;
	}
	private long id;
	private long class_id;
	private Date date; 
	private String image_names; //一天所有的照片内容 分号分离
	
}
