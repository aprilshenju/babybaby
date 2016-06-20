package com.umeijia.vo;

import java.util.Date;

public class Administrator {
	
	private long id;
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
	public boolean isIs_super() {
		return is_super;
	}
	public void setIs_super(boolean is_super) {
		this.is_super = is_super;
	}
	private String phone_num;
	private String pwd_md;
	private String name;
	private boolean is_super;
	
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

	private Date expire; // 过期时间
	private String token; //登陆token

	public  Administrator(String phone,String email,String pwd,String name,Date date,boolean super_root){
			phone_num=phone;
			this.email=email;
			pwd_md=pwd;
			this.name=name;
			regist_date=date;
			this.is_super=super_root;

	}


}
