package com.umeijia.vo;

import java.util.Date;

public class DailyLog {
	//运维人员的编辑操作记录，比如添加幼儿园，添加摄像头
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getLog_date() {
		return log_date;
	}
	public void setLog_date(Date log_date) {
		this.log_date = log_date;
	}
	public int getUser_type() {
		return user_type;
	}
	public void setUser_type(int user_type) {
		this.user_type = user_type;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public String getOp_type() {
		return op_type;
	}
	public void setOp_type(String op_type) {
		this.op_type = op_type;
	}
	public String getOp_content() {
		return op_content;
	}
	public void setOp_content(String op_content) {
		this.op_content = op_content;
	}
	public String getOp_object() {
		return op_object;
	}
	public void setOp_object(String op_object) {
		this.op_object = op_object;
	}
	private Date log_date;
	private int user_type; // 用户身份类型
	private long user_id; //用户id，agent或administrator的id
	private String op_type; // 操作类型
	private String op_content; // 操作类容 
	private String op_object; //操作对象 ，添加的幼儿园的id，摄像头的id
}
