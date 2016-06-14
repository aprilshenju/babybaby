package com.umeijia.vo;

import java.util.Date;
import java.util.Set;

public class Teacher {
	private long id;
	private String name;
	private String avatar_path;
	private String pwd_md;
	private long garten_id;
	private String phone_num;
	private String description;
	private Set<Long> class_ids;
	private boolean is_leader;
	private String wishes;
	
	public String getWishes() {
		return wishes;
	}
	public void setWishes(String wishes) {
		this.wishes = wishes;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getRegist_date() {
		return regist_date;
	}
	public void setRegist_date(Date regist_date) {
		this.regist_date = regist_date;
	}
	private String email;
	private Date regist_date;
	
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
	public String getAvatar_path() {
		return avatar_path;
	}
	public void setAvatar_path(String avatar_path) {
		this.avatar_path = avatar_path;
	}
	public String getPwd_md() {
		return pwd_md;
	}
	public void setPwd_md(String pwd_md) {
		this.pwd_md = pwd_md;
	}
	public long getGarten_id() {
		return garten_id;
	}
	public void setGarten_id(long school_id) {
		this.garten_id = school_id;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set<Long> getClass_ids() {
		return class_ids;
	}
	public void setClass_ids(Set<Long> class_ids) {
		this.class_ids = class_ids;
	}
	public boolean isIs_leader() {
		return is_leader;
	}
	public void setIs_leader(boolean is_leader) {
		this.is_leader = is_leader;
	}
	
	//测试提交——ly


}
