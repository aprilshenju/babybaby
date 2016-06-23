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

	public Kindergarten getGarten() {
		return garten;
	}

	public void setGarten(Kindergarten garten) {
		this.garten = garten;
	}

	private String name;
	private Kindergarten garten;

	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	public void setCameras(Set<Camera> cameras) {
		this.cameras = cameras;
	}

	public void setTeachers(Set<Teacher> teachers) {
		this.teachers = teachers;
	}

	public Set<Student> getStudents() {

		return students;
	}

	public Set<Camera> getCameras() {
		return cameras;
	}

	public Set<Teacher> getTeachers() {
		return teachers;
	}

	private Set<Student> students;
	private Set<Camera> cameras;
	private Set<Teacher> teachers;

	/*
 *  时间概念，系统只需要最新的当天的相关记录。历史所有记录无意义
 * private Set<Long> homework_ids;
	private Set<Long> checkin_ids;
	private Set<Long> activity_ids; //班级活动集合
*/

	public String getClass_introduction() {
		return class_introduction;
	}

	public void setClass_introduction(String class_introduction) {
		this.class_introduction = class_introduction;
	}

	/*	public Set<Long> getActivity_ids() {
                return activity_ids;
            }
            public void setActivity_ids(Set<Long> activity_ids) {
                this.activity_ids = activity_ids;
            }
        */
	private  String class_introduction;
	private String course_schedule; //课程表
	private String teachers_contacts; //联系方式
	private String parents_contacts; // 家长联系方式
}
