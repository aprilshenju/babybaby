package com.umeijia.vo;

import java.util.Date;

public class Parents {

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public String getPwd_md() {
		return pwd_md;
	}
	public void setPwd_md(String pwd_md) {
		this.pwd_md = pwd_md;
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
	public long getStu_id() {
		return stu_id;
	}
	public void setStu_id(long stu_id) {
		this.stu_id = stu_id;
	}
	public long getClass_id() {
		return class_id;
	}
	public void setClass_id(long class_id) {
		this.class_id = class_id;
	}
	public boolean isIs_vip() {
		return is_vip;
	}
	public void setIs_vip(boolean is_vip) {
		this.is_vip = is_vip;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
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
	public boolean isAllow_app_push() {
		return allow_app_push;
	}
	public void setAllow_app_push(boolean allow_app_push) {
		this.allow_app_push = allow_app_push;
	}
	public boolean isAllow_wechat_push() {
		return allow_wechat_push;
	}
	public void setAllow_wechat_push(boolean allow_wechat_push) {
		this.allow_wechat_push = allow_wechat_push;
	}
	private long id;
	private String phone_num;
	private String pwd_md;
	private String name;

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	private String relationship;
	private String avatar_path;
	private long stu_id;
	private long class_id; // 暂时无法支持多个学生？

	public void setVip_start(Date vip_start) {
		this.vip_start = vip_start;
	}

	public void setVip_end(Date vip_end) {
		this.vip_end = vip_end;
	}

	private boolean is_vip;

	public Date getVip_end() {
		return vip_end;
	}

	public boolean is_vip() {
		return is_vip;
	}

	public Date getVip_start() {
		return vip_start;
	}

	private  Date vip_start;
	private Date vip_end; //会员开始结束时间
	private int gender;
	private String email;
	private Date regist_date;
	
	private boolean allow_app_push; // 家长是否允许app推送
	private boolean allow_wechat_push; // 家长是否允许微信推送 
	
}
