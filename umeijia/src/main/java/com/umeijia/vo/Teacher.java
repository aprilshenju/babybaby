package com.umeijia.vo;

import java.util.Date;
import java.util.Set;

public class Teacher {
	private long id;
	private String name;
	private String avatar_path;
	private String pwd_md;
	//	private long garten_id;
	private Kindergarten kindergarten;
	private String phone_num;
	private String description;
	private String email;
	private Date regist_date;
	private Set<GartenNews>gartenNewses;
	private Date expire; // 过期时间
	private String token; //登陆token
	private  boolean valid;

	public  Teacher(){

	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Teacher(long id){
		this.id=id;

	}

	public Teacher(String name,String avatar,String pwd_md,Kindergarten garten,String phone,String descrip,String email,boolean leader,String wishes,String gender){
		this.name=name;
		this.avatar_path=avatar;
		this.pwd_md=pwd_md;
		this.kindergarten=garten;
		this.phone_num=phone;
		this.description=descrip;
		this.email=email;
		this.regist_date=new Date();
		this.is_leader=leader;
		this.wishes=wishes;
		valid=true;
		this.gender=gender;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	private String gender;

	public Kindergarten getKindergarten() {
		return kindergarten;
	}
	public void setKindergarten(Kindergarten kindergarten) {
		this.kindergarten = kindergarten;
	}
	public void setClasses(Set<Class> classes) {
		this.classes = classes;
	}
	private Set<Class> classes;
	public Set<Class> getClasses() {
		return classes;
	}
	public Boolean getIs_leader() {
		return is_leader;
	}
	public void setIs_leader(Boolean is_leader) {
		this.is_leader = is_leader;
	}
	private Boolean is_leader;
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
	public void setGartenNewses(Set<GartenNews> gartenNewses) {
		this.gartenNewses = gartenNewses;
	}
	public Set<GartenNews> getGartenNewses() {
		return gartenNewses;
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

	//测试提交——ly


}
