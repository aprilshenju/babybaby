package com.umeijia.vo;

import java.util.Date;
import java.util.Set;

public class Student {
	private long id;
	private String name;
	private String nick_name;
	private String gender;
	private Class cla;
	private Set<Parents> parents;
	private Date entrance_date;
	private Date birthday;
	private int height;
	private int weight;
	private String avatar_path;
	private  boolean vip; //登陆就得反查
	private  Date vip_start;
	private Date vip_end; //会员开始结束时间
	private  boolean valid;

	public long getSchool_id() {
		return school_id;
	}

	public void setSchool_id(long school_id) {
		this.school_id = school_id;
	}

	private long school_id;
	public Student(){

	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public  Student(String name, String nick, String gender, Date birthday, int height, int weight, String avatar, Class cla, boolean isvip, Date start, Date end, Date enter_date){
		vip=isvip;
		this.vip_start=start;
		this.vip_end=end;
		this.name=name;
		this.nick_name=nick;
		this.gender=gender;
		this.birthday= birthday;
		this.height=height;
		this.weight=weight;

		this.avatar_path=avatar;
		this.cla=cla;
		this.entrance_date=enter_date;
		valid=true; //新创建的账号默认有效
	}


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
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Date getEntrance_date() {
		return entrance_date;
	}
	public void setEntrance_date(Date entrance_date) {
		this.entrance_date = entrance_date;
	}
	public Date getBirthday() {
		return birthday;
	}
	public int getHeight() {
		return height;
	}
	public int getWeight() {
		return weight;
	}
	public String getAvatar_path() {
		return avatar_path;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public void setAvatar_path(String avatar_path) {
		this.avatar_path = avatar_path;
	}
	public void setCla(Class cla) {
		this.cla = cla;
	}
	public Class getCla() {
		return cla;
	}
	public Set<Parents> getParents() {
		return parents;
	}
	public void setParents(Set<Parents> parents) {
		this.parents = parents;
	}
	public boolean isVip() {
		return vip;
	}
	public void setVip(boolean vip) {
		this.vip = vip;
	}
	public Date getVip_start() {
		return vip_start;
	}
	public Date getVip_end() {
		return vip_end;
	}
	public void setVip_start(Date vip_start) {
		this.vip_start = vip_start;
	}
	public void setVip_end(Date vip_end) {
		this.vip_end = vip_end;
	}




}
