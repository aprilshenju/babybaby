package com.umeijia.vo;

import java.util.Date;
import java.util.Set;

public class Student {

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
	public long getClass_id() {
		return class_id;
	}
	public void setClass_id(long class_id) {
		this.class_id = class_id;
	}
	public Set<Long> getParents_ids() {
		return parents_ids;
	}
	public void setParents_ids(Set<Long> parents_ids) {
		this.parents_ids = parents_ids;
	}
	public Date getEntrance_date() {
		return entrance_date;
	}
	public void setEntrance_date(Date entrance_date) {
		this.entrance_date = entrance_date;
	}
	private long id;
	private String name;
	private String nick_name;
	private int gender;
	private int age;

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
	private Date birthday;
	private int height;
	private int weight;
	private String avatar_path;

	private long class_id;
	private Set<Long> parents_ids;
	private Date entrance_date;

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

	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	private  boolean vip; //登陆就得反查





}
