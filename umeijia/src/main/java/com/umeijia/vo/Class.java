package com.umeijia.vo;

import java.util.LinkedHashSet;
import java.util.Set;

public class Class {
	private  String class_introduction;
	private String course_schedule; //课程表
	private String teachers_contacts; //联系方式
	private String parents_contacts; // 家长联系方式
	private long id;
	private Set<Student> students;
	private Set<Camera> cameras;
	private Set<Teacher> teachers;
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
	private String name;
	private Kindergarten garten;
	public Set<Camera> getCameras() {
		return cameras;
	}
	public Set<Teacher> getTeachers() {
		return teachers;
	}


	public Class(){

	}
	public  Class(String name,String class_introduction,String schedule,String tcontacts,String pcontacts,Kindergarten garten){
		this.name=name;
		this.class_introduction=class_introduction;
		this.course_schedule = schedule;
		this.teachers_contacts=tcontacts;
		this.parents_contacts=pcontacts;
		this.garten=garten;
		this.teachers = new LinkedHashSet<Teacher>();
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
	public Kindergarten getGarten() {
		return garten;
	}
	public void setGarten(Kindergarten garten) {
		this.garten = garten;
	}
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
	public String getClass_introduction() {
		return class_introduction;
	}
	public void setClass_introduction(String class_introduction) {
		this.class_introduction = class_introduction;
	}


}
