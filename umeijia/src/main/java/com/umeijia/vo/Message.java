package com.umeijia.vo;

import java.util.Date;

/**
 * 留言
 * **/
public class Message {
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public int getContent_type() {
		return content_type;
	}
	public void setContent_type(int content_type) {
		this.content_type = content_type;
	}
	public long getTeacher_id() {
		return teacher_id;
	}
	public void setTeacher_id(long teacher_id) {
		this.teacher_id = teacher_id;
	}
	public long getParents_id() {
		return parents_id;
	}
	public void setParents_id(long parents_id) {
		this.parents_id = parents_id;
	}
	public int getSend_direction() {
		return send_direction;
	}
	public void setSend_direction(int send_direction) {
		this.send_direction = send_direction;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	private long id;
	private String content;
	private String image_url;
	private int content_type; // 图片还是文字  1-文字，2-图片
	private long teacher_id;
	private long parents_id;
	private int send_direction; // 1-家长发给老师的，2-老师发给家长的
	private Date date;
}
