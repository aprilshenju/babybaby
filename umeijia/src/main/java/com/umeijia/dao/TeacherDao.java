package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.util.MD5;
import com.umeijia.vo.Teacher;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by shenju on 2016/6/14.
 * 待优化问题：目前更新属性值，是先查询对象，然后更新整个对象的。并不高效。
 * 应当是直接修改字段，用update操作
 *
 *
 */
@Scope("prototype")
@Repository("teacherdao")
public class TeacherDao {
    public TeacherDao(){

    }

    public Teacher queryTeacher(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Teacher as u where u.id=%d",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Teacher teacher = (Teacher) list.get(0);
            return teacher;
        }else {
            return null;
        }
    }

    public Teacher queryTeacher(String phoneNumber) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Teacher as u where u.phone_num=\'%s\'", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Teacher teacher = (Teacher) list.get(0);
            return teacher;
        }else {
            return null;
        }
    }

    public Teacher queryTeacherByEmail(String email) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Teacher as u where u.email=\'%s\'", email);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Teacher teacher = (Teacher) list.get(0);
            return teacher;
        }else {
            return null;
        }
    }

    public boolean verifyToken(long id,String tkn){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("select u.expire from Teacher as u where u.id=%d and u.token=\'%s\'",id,tkn);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Date dead=(Date)list.get(0);
            Date now=new Date();
            if(dead.after(now)){
                return  true;  //还有效
            }
        }
        return  false;
    }
    /**
     * 登陆成功，设置tkn和有效时间
     * **/
    public Teacher loginCheckByPhone(String phoneNumber, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Teacher as u where u.phone_num=\'%s\'", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        if(list.size()>0) {
            Teacher teacher = (Teacher) list.get(0);
            if(passwd.equals(teacher.getPwd_md())){ //密码匹配
                try {
                    session.setFlushMode(FlushMode.AUTO);
                    session.beginTransaction();
                    teacher.setToken(MD5.GetSaltMD5Code(teacher.getPhone_num()+passwd+new Date().toString())); //登陆时，即重新计算token，保存数据库
                    Date now=new Date();
                    Date dead = new Date(now .getTime() + 7200000); //两个小时有效期
                    teacher.setExpire(dead);
                    session.update(teacher);
                    session.flush();
                    session.getTransaction().commit();
                } catch (HibernateException e) {
                    e.printStackTrace();
                    session.getTransaction().rollback();
                    teacher=null;
                } finally{
                    session.close();
                    return teacher;
                }
            }
        }
        return null;
    }

    /**
     * 登陆成功，设置tkn和有效时间
     * **/
    public Teacher loginCheckByEmail(String email, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Teacher as u where u.email=\'%s\'", email);
        Query query = session.createQuery(sql);
        List list = query.list();
        if(list.size()>0) {
            Teacher teacher = (Teacher) list.get(0);
            if(passwd.equals(teacher.getPwd_md())){ //密码匹配
                try {
                    session.setFlushMode(FlushMode.AUTO);
                    session.beginTransaction();
                    teacher.setToken(MD5.GetSaltMD5Code(teacher.getPhone_num()+passwd+new Date().toString())); //登陆时，即重新计算token，保存数据库
                    Date now=new Date();
                    Date dead = new Date(now .getTime() + 7200000); //两个小时有效期
                    teacher.setExpire(dead);
                    session.update(teacher);
                    session.flush();
                    session.getTransaction().commit();
                } catch (HibernateException e) {
                    e.printStackTrace();
                    session.getTransaction().rollback();
                    teacher=null;
                } finally{
                    session.close();
                    return teacher;
                }
            }
        }
        return null;
    }


    public boolean addTeacher(Teacher teacher) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(teacher);
            session.flush();
            session.getTransaction().commit();
            result=true;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            result=false;
        } finally{
            session.close();
            return result;
        }
    }

    public boolean updateTeacher(Teacher teacher) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(teacher);
            session.flush();
            session.getTransaction().commit();
            result=true;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            result=false;
        } finally{
            session.close();
            return result;
        }
    }

/**
 * 一个幼儿园的所有老师集合
 * **/
    public List<Teacher> queryTeachersByGarten(long gartenID) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Teacher as u where u.kindergarten.id=%d",gartenID);
        Query query = session.createQuery(sql);
        List list =query.list();
        List<Teacher> teachers=new ArrayList<Teacher>();
        session.close();
        for (int i = 0; i < list.size(); i++){
            Teacher t=(Teacher)list.get(i);
            teachers.add(t);
        }
        return teachers;
    }

    public boolean deleteTeacher(String phoneNumber) {
        boolean result=false;
        Teacher teacher = queryTeacher(phoneNumber);
        if(teacher!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(teacher);
                session.flush();
                session.getTransaction().commit();
                result=true;
            } catch (HibernateException e) {
                e.printStackTrace();
                session.getTransaction().rollback();
                result=false;
            } finally{
                session.close();
                return result;
            }
        }
        return false;
    }
}
