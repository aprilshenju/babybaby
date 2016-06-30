package com.umeijia.vo;

import java.util.Date;

public class ClassNotification {
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}

	public ClassNotification(String title, String description, String image_urls, Date date, long teacher_id, long class_id, String subscribers) {
		this.title = title;
		this.description = description;
		this.image_urls = image_urls;
		this.date = date;
		this.teacher_id = teacher_id;
		this.class_id = class_id;
		this.subscribers = subscribers;
	}

	public ClassNotification() {
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
	public String getSubscribers() {
		return subscribers;
	}
	public void setSubscribers(String subscribers) {
		this.subscribers = subscribers;
	}
	private long id;
	private String title;
	private String description;
	private String image_urls; // 分号拼接在一起。
	private Date date;
	private long teacher_id;
	private long class_id;

	private String subscribers; // 分号分割,已查看通知的宝贝集合
	
	
}