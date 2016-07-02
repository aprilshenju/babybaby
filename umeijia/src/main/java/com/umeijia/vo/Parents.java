package com.umeijia.vo;

import java.util.Date;

public class Parents {
	private int gender;
	private String email;
	private Date regist_date;
	private boolean allow_app_push; // 家长是否允许app推送
	private boolean allow_wechat_push; // 家长是否允许微信推送
	private Student student;
	private long class_id; // 暂时无法支持多个学生？
	private long garten_id;
	private long id;
	private String phone_num;
	private String pwd_md;
	private String name;
	private String relationship;
	private String wechat_open_id;

	public String getWechat_open_id() {
		return wechat_open_id;
	}

	public void setWechat_open_id(String wechat_open_id) {
		this.wechat_open_id = wechat_open_id;
	}

	public void setGarten_id(long garten_id) {
		this.garten_id = garten_id;
	}

	private String avatar_path;

	public long getGarten_id() {
		return garten_id;
	}

	private Date expire; // 过期时间
	private String token; //登陆token
	private  boolean valid;
    public Parents(){

    }

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Parents(String phone, String email, String name, Student stu, long cla_id, String pwd, String relation, String avatar){
		this.phone_num=phone;
		this.email=email;
		this.name=name;

		this.student=stu;
		this.class_id=cla_id;
		this.pwd_md=pwd;
		this.relationship=relation;
		this.avatar_path=avatar;
		regist_date=new Date();
		expire=regist_date;
		token="1";
		valid=true;
	}

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
	public long getClass_id() {
		return class_id;
	}
	public void setClass_id(long class_id) {
		this.class_id = class_id;
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
	public String getRelationship() {
		return relationship;
	}
	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public Student getStudent() {
		return student;
	}
	public Date getExpire() {
		return expire;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public void setExpire(Date expire) {
		this.expire = expire;
	}


}
