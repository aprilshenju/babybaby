package com.umeijia.vo;

import java.util.Date;
import java.util.Set;

public class Kindergarten {
	private String addr;
	private String contact_num;
	private String description;
	private String teacher_presence_imgs; // 教师风采图片列表
	private String garten_instrument_imgs; // 教学设施列表
	private String garten_presence_imgs; //幼儿园图片展示列表
	private String leader_wishes;
	private Date create_date;
	private  String teacher_contacts; //老师联系方式
	private Long id;

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	private String name;
	private Agent agent; //代理商id
	private Set<Camera> cameras;
	private Set<Class> classes; // 班级列表
	private Set<Teacher> teachers;

	private boolean valid;

	public void setTeacher_contacts(String teacher_contacts) {
		this.teacher_contacts = teacher_contacts;
	}

	private Set<GartenNews> news;// 新闻列表
	private long leader_id; //园长信息

	public String getTeacher_contacts() {
		return teacher_contacts;
	}

	public Kindergarten(){

	}

	public  Kindergarten(String name,String addr,String contact_num,String description,String t_imgs,String gi_imgs,String gp_imgs,Agent agent){
		leader_id=0;
		teacher_contacts="";//初始化时，没有老师
		this.agent=agent;
		this.name=name;
		this.addr=addr;
		this.contact_num=contact_num;
		this.description=description;
		this.teacher_presence_imgs=t_imgs;
		this.garten_instrument_imgs=gi_imgs;
		this.garten_presence_imgs=gp_imgs;
		this.create_date=new Date();
		this.leader_wishes="";
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getContact_num() {
		return contact_num;
	}
	public void setContact_num(String contact_num) {
		this.contact_num = contact_num;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getLeader_id() {
		return leader_id;
	}
	public void setLeader_id(long leader_id) {
		this.leader_id = leader_id;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	public Agent getAgent() {
		return agent;
	}
	public void setCameras(Set<Camera> cameras) {
		this.cameras = cameras;
	}
	public void setClasses(Set<Class> classes) {
		this.classes = classes;
	}
	public void setTeachers(Set<Teacher> teachers) {
		this.teachers = teachers;
	}
	public void setNews(Set<GartenNews> news) {
		this.news = news;
	}
	public void setLeader_wishes(String leader_wishes) {
		this.leader_wishes = leader_wishes;
	}
	public String getLeader_wishes() {
		return leader_wishes;
	}
	public String getTeacher_presence_imgs() {
		return teacher_presence_imgs;

	}
	public String getGarten_instrument_imgs() {
		return garten_instrument_imgs;
	}
	public void setTeacher_presence_imgs(String teacher_presence_imgs) {
		this.teacher_presence_imgs = teacher_presence_imgs;
	}

	public void setGarten_instrument_imgs(String garten_instrument_imgs) {
		this.garten_instrument_imgs = garten_instrument_imgs;
	}

	public void setGarten_presence_imgs(String garten_presence_imgs) {
		this.garten_presence_imgs = garten_presence_imgs;
	}

	public String getGarten_presence_imgs() {
		return garten_presence_imgs;
	}

	public Set<Camera> getCameras() {
		return cameras;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
	public Date getCreate_date() {
		return create_date;
	}

	public Set<Class> getClasses() {
		return classes;
	}

	public Set<Teacher> getTeachers() {
		return teachers;
	}

	public Set<GartenNews> getNews() {
		return news;
	}




}
