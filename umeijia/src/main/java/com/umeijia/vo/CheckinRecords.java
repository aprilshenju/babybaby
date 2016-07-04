package com.umeijia.vo;

import java.util.Date;

public class CheckinRecords {
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}


	public long getStu_id() {
		return stu_id;
	}
	public void setStu_id(long stu_id) {
		this.stu_id = stu_id;
	}
	public long getClass_id() {
		return class_id;
	}
	public void setClass_id(long class_id) {
		this.class_id = class_id;
	}


	private long id;
	private Date date;


    public String getImage_path_2() {
        return image_path_2;
    }

    public void setImage_path_2(String image_path_2) {
        this.image_path_2 = image_path_2;
    }

    public String getImage_path_3() {
        return image_path_3;
    }

    public void setImage_path_3(String image_path_3) {
        this.image_path_3 = image_path_3;
    }

    public String getImage_path_4() {
        return image_path_4;
    }

    public void setImage_path_4(String image_path_4) {
        this.image_path_4 = image_path_4;
    }

    public String getState_1() {
        return state_1;
    }

    public void setState_1(String state_1) {
        this.state_1 = state_1;
    }

    public String getState_2() {
        return state_2;
    }

    public void setState_2(String state_2) {
        this.state_2 = state_2;
    }

    public String getState_3() {
        return state_3;
    }

    public void setState_3(String state_3) {
        this.state_3 = state_3;
    }

    public String getState_4() {
        return state_4;
    }

    public void setState_4(String state_4) {
        this.state_4 = state_4;
    }

    public float getTemperature_1() {
        return temperature_1;
    }

    public void setTemperature_1(float temperature_1) {
        this.temperature_1 = temperature_1;
    }

    public float getTemperature_2() {
        return temperature_2;
    }

    public void setTemperature_2(float temperature_2) {
        this.temperature_2 = temperature_2;
    }

    public float getTemperature_3() {
        return temperature_3;
    }

    public void setTemperature_3(float temperature_3) {
        this.temperature_3 = temperature_3;
    }

    public float getTemperature_4() {
        return temperature_4;
    }

    public void setTemperature_4(float temperature_4) {
        this.temperature_4 = temperature_4;
    }

    public String getImage_path_1() {
        return image_path_1;
    }

    public void setImage_path_1(String image_path_1) {
        this.image_path_1 = image_path_1;
    }

    private String image_path_1;
    private String image_path_2;
    private String image_path_3;
    private String image_path_4;
	private long stu_id;
	private long class_id;
	private String state_1; //签到状态
    private String state_2; //签到状态
    private String state_3; //签到状态
    private String state_4; //签到状态

	private float temperature_1; //体温数据
    private float temperature_2; //体温数据
    private float temperature_3; //体温数据
    private float temperature_4; //体温数据
}
