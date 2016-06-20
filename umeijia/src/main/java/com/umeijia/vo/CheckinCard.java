package com.umeijia.vo;

public class CheckinCard {
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getStu_id() {
		return stu_id;
	}
	public void setStu_id(long stu_id) {
		this.stu_id = stu_id;
	}
	public String getIs_valid() {
		return is_valid;
	}
	public void setIs_valid(String is_valid) {
		this.is_valid = is_valid;
	}

	private long id;
	private long stu_id;
	private String is_valid;
	private long class_id;
	public long getClass_id() {
		return class_id;
	}
	public void setClass_id(long class_id) {
		this.class_id = class_id;
	}
}
