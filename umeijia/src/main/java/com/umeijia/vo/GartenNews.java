package com.umeijia.vo;

import java.util.Date;

public class GartenNews {
	private long id;
	private String title;
	private String summary;
	private String description;
	private String image_urls;
	private Date publishDate;
	private Date modifyDate;
	private Teacher teacher;  // 老师发布的新闻
	private Kindergarten kindergarten;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    private boolean valid;

	public  GartenNews(){
        this.valid=true;
	}

	public  GartenNews(String title,String summary,String descrip,String img_urls,Teacher te,Kindergarten garten){
			this.title=title;
			this.summary=summary;
			this.description=descrip;
			this.image_urls=img_urls;
			this.publishDate=new Date();
			this.modifyDate=new Date();
			this.teacher=te;
			this.kindergarten= garten;
	}

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
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
	public Teacher getTeacher() {
		return teacher;

	}
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	public Kindergarten getKindergarten() {
		return kindergarten;
	}
	public void setKindergarten(Kindergarten kindergarten) {
		this.kindergarten = kindergarten;
	}
	
}