package com.umeijia.vo;

import java.util.Set;

public class Kindergarten {
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
	public long getAgent_id() {
		return agent_id;
	}
	public void setAgent_id(long agent_id) {
		this.agent_id = agent_id;
	}
	public Set<Long> getCamera_ids() {
		return camera_ids;
	}
	public void setCamera_ids(Set<Long> camera_ids) {
		this.camera_ids = camera_ids;
	}
	public Set<Long> getClass_ids() {
		return class_ids;
	}
	public void setClass_ids(Set<Long> class_ids) {
		this.class_ids = class_ids;
	}
	public Set<Long> getTeacher_ids() {
		return teacher_ids;
	}
	public void setTeacher_ids(Set<Long> teacher_ids) {
		this.teacher_ids = teacher_ids;
	}
	public Set<Long> getNews_ids() {
		return news_ids;
	}
	public void setNews_ids(Set<Long> news_ids) {
		this.news_ids = news_ids;
	}
	public long getLeader_id() {
		return leader_id;
	}
	public void setLeader_id(long leader_id) {
		this.leader_id = leader_id;
	}
	private long id;
	private String name;
	private String addr;
	private String contact_num;
	private String description;
	private long agent_id; //代理商id
	private Set<Long> camera_ids;
	private Set<Long> class_ids; // 班级列表
	private Set<Long> teacher_ids;
	private Set<Long> news_ids;// 新闻列表 
	private long leader_id; //园长信息

}
