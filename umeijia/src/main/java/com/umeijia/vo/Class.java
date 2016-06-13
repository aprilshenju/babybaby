package com.umeijia.vo;

import java.util.Set;

public class Class {
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
	public long getSchool_id() {
		return school_id;
	}
	public void setSchool_id(long school_id) {
		this.school_id = school_id;
	}
	public Set<Long> getStu_ids() {
		return stu_ids;
	}
	public void setStu_ids(Set<Long> stu_ids) {
		this.stu_ids = stu_ids;
	}
	public Set<Long> getCamera_ids() {
		return camera_ids;
	}
	public void setCamera_ids(Set<Long> camera_ids) {
		this.camera_ids = camera_ids;
	}
	public Set<Long> getTeacher_ids() {
		return teacher_ids;
	}
	public void setTeacher_ids(Set<Long> teacher_ids) {
		this.teacher_ids = teacher_ids;
	}

	public String getCourse_schedule() {
		return course_schedule;
	}
	public void setCourse_schedule(String course_schedule) {
		this.course_schedule = course_schedule;
	}
	public String getTeachers_contacts() {
		return teachers_contacts;
	}
	public void setTeachers_contacts(String teachers_contacts) {
		this.teachers_contacts = teachers_contacts;
	}
	public String getParents_contacts() {
		return parents_contacts;
	}
	public void setParents_contacts(String parents_contacts) {
		this.parents_contacts = parents_contacts;
	}
	private long id;
	private String name;
	private long school_id;
	private Set<Long> stu_ids;
	private Set<Long> camera_ids;
	private Set<Long> teacher_ids;
/*	
 *  时间概念，系统只需要最新的当天的相关记录。历史所有记录无意义
 * private Set<Long> homework_ids;
	private Set<Long> checkin_ids;
	private Set<Long> activity_ids; //班级活动集合
*/
	private Set<Long> food_ids; //班级饮食集合 
	
/*	public Set<Long> getActivity_ids() {
		return activity_ids;
	}
	public void setActivity_ids(Set<Long> activity_ids) {
		this.activity_ids = activity_ids;
	}
*/
	public Set<Long> getFood_ids() {
		return food_ids;
	}
	public void setFood_ids(Set<Long> food_ids) {
		this.food_ids = food_ids;
	}

	
	private String course_schedule; //课程表
	private String teachers_contacts; //联系方式
	private String parents_contacts; // 家长联系方式
}
