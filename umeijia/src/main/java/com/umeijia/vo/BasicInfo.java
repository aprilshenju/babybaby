package com.umeijia.vo;

/**
 * Created by shenju on 2016/6/16.
 */
public class BasicInfo {

    public String getAddr() {
        return addr;
    }

    public long getId() {
        return id;
    }

    public String getVersion_no() {
        return version_no;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getContact() {
        return contact;
    }

    public String getQq() {
        return qq;
    }

    public String getEmail() {
        return email;
    }

    public String getIntroduction() {
        return introduction;
    }
    private long id;
    private String version_no;

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setVersion_no(String version_no) {
        this.version_no = version_no;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    private String company_name;
    private  String contact;
    private  String qq;
    private String email;
    private  String introduction;
    private  String addr;

}
