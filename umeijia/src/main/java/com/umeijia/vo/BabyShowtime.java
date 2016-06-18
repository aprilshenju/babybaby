package com.umeijia.vo;

import java.util.Date;
import java.util.Set;

public class BabyShowtime {
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public long getClass_id() {
		return class_id;
	}
	public void setClass_id(long class_id) {
		this.class_id = class_id;
	}
	public long getBaby_id() {
		return baby_id;
	}
	public void setBaby_id(long baby_id) {
		this.baby_id = baby_id;
	}
	public long getTeacher_id() {
		return teacher_id;
	}
	public void setTeacher_id(long teacher_id) {
		this.teacher_id = teacher_id;
	}
	public long getParent_id() {
		return parent_id;
	}
	public void setParent_id(long parent_id) {
		this.parent_id = parent_id;
	}
	public Set<Long> getComments_ids() {
		return comments_ids;
	}
	public void setComments_ids(Set<Long> comments_ids) {
		this.comments_ids = comments_ids;
	}
	private long id;
	private String description;
	private String image_urls;
	private Date date;
	private long class_id;
	private long baby_id;
	private long teacher_id; // 动态是由 老师或家长发布的
	private long parent_id;
	private Set<Long> comments_ids;

	// 足迹要按月查历史。。。？？？

	public int getShow_type() {
		return show_type;
	}

	public void setShow_type(int show_type) {
		this.show_type = show_type;
	}

	private int show_type; // 1 图文 2 视频
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	private boolean valid; //是否删除
	
}