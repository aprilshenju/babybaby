package com.umeijia.vo;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 根据时间段和class_id更新 饮食记录。只维持最新的。
 * ***/
public class FoodRecord {

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getClass_id() {
		return class_id;
	}
	public void setClass_id(long class_id) {
		this.class_id = class_id;
	}
	public long getSchool_id() {
		return school_id;
	}
	public void setSchool_id(long school_id) {
		this.school_id = school_id;
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
	public int getTime_area() {
		return time_area;
	}
	public void setTime_area(int time_area) {
		this.time_area = time_area;
	}
	private long id;
	private String name;
	private long class_id;
	private long school_id;
	private String image_urls;
	private Date date; //哪一天
	private int time_area; //什么时候的食物，早餐、中餐、晚餐
	
	
}
