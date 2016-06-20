package com.umeijia.vo;

import java.util.Date;

public class GartenNews {
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
	public long getTeacher_id() {
		return teacher_id;
	}
	public void setTeacher_id(long teacher_id) {
		this.teacher_id = teacher_id;
	}
	/*public long getSchool_id() {
		return school_id;
	}
	public void setSchool_id(long school_id) {
		this.school_id = school_id;
	}*/
	private long id;
	private String title;
	private String summary;
	private String description;
	private String image_urls;
	private Date publishDate;
	private Date modifyDate;

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	private long teacher_id;  // 老师发布的新闻

	public Kindergarten getKindergarten() {
		return kindergarten;
	}

	public void setKindergarten(Kindergarten kindergarten) {
		this.kindergarten = kindergarten;
	}

	private Kindergarten kindergarten;
	
	
}