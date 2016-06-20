package com.umeijia.vo;

import java.util.Set;

public class Kindergarten {
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

	private Long id;
	private String name;

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

	private String addr;
	private String contact_num;
	private String description;

	public Set<Camera> getCameras() {
		return cameras;
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

	private Agent agent; //代理商id
	private Set<Camera> cameras;
	private Set<Class> classes; // 班级列表
	private Set<Teacher> teachers;
	private Set<GartenNews> news;// 新闻列表
	private long leader_id; //园长信息

}
