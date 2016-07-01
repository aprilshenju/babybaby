package com.umeijia.vo;

import java.util.Date;

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
	public String getRecords() {
		return records;
	}
	public void setRecords(String records) {
		this.records = records;
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
	private long id;
	private String records;

//	public Class getCla() {
//		return cla;
//	}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    //	private Class cla;
    private Date date;
	private long school_id;

    public long getClass_id() {
        return class_id;
    }

    public void setClass_id(long class_id) {
        this.class_id = class_id;
    }

    private long class_id;
	private String image_urls;



//	public void setCla(Class cla) {
//		this.cla = cla;
//	}
	// 分号隔离每天的数据，逗号隔离每顿的数据

/*	private Date date; //哪一天*/
/*	private int time_area; //什么时候的食物，早餐、中餐、晚餐*/
	
	
}
