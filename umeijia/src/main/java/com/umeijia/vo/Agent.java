package com.umeijia.vo;

import java.util.Date;
import java.util.Set;

public class Agent {
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
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public float getPrice_rate() {
		return price_rate;
	}
	public void setPrice_rate(float price_rate) {
		this.price_rate = price_rate;
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
	private long id;
	private String name;
	private String pwd_md;
	private String phone_num;

	public void setAvarta(String avarta) {
		this.avarta = avarta;
	}

	public String getAvarta() {

		return avarta;
	}

	private  String avarta;


	public Set<Kindergarten> getGartens() {
		return gartens;
	}

	private Set<Kindergarten> gartens;

	public void setGartens(Set<Kindergarten> gartens) {
		this.gartens = gartens;
	}

	private String company_name;
	private float price_rate;

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

	public  Agent(){

	}

	public  Agent(long id){
		this.id=id;
	}

	public Agent(String phone,String email,String pwd,String name,Date date,String company,float price){
		phone_num=phone;
		this.email=email;
		pwd_md=pwd;
		this.name=name;
		regist_date=date;
		company_name=company;
		price_rate=price;

	}

	
}
