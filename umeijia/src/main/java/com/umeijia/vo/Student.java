package com.umeijia.vo;

import java.util.Date;
import java.util.Set;

public class Student {
	private long id;
	private String name;
	private String nick_name;
	private int gender;
	private int age;
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

	public Student(){

	}
	public  Student(String name,String nick,int gender,int age,Date birthday,int height,int weight,String avatar,Class cla){
		vip=false;
		this.name=name;
		this.nick_name=nick_name;
		this.gender=gender;
		this.age=age;
		this.birthday= birthday;
		this.height=height;
		this.weight=weight;
		this.avatar_path=avatar;
		this.cla=cla;
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
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
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
