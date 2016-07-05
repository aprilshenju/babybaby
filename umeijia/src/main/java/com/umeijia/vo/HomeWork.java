package com.umeijia.vo;

import java.util.Date;

public class HomeWork {

	public HomeWork(long class_id, String title, String description, String image_urls, Date date, long teacher_id) {
		this.class_id = class_id;
		this.title = title;
		this.description = description;
		this.image_urls = image_urls;
		this.date = date;
		this.teacher_id = teacher_id;
	}

	public HomeWork(){
        this.valid=true;
	}



	private long id;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage_urls() {
		return image_urls;
	}
	public void setImage_urls(String image_urls) {
		this.image_urls = image_urls;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public long getTeacher_id() {
		return teacher_id;
	}
	public void setTeacher_id(long teacher_id) {
		this.teacher_id = teacher_id;
	}
	public long getClass_id() {
		return class_id;
	}
	public void setClass_id(long class_id) {
		this.class_id = class_id;
	}
	private String title;
	private String description;
	private String image_urls; //多个链接，;号分割
	private Date date;
	private long teacher_id;
	private long class_id;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    private boolean valid;
}
