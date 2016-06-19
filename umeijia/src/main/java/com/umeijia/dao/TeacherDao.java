package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.Teacher;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    public boolean loginCheck(String phoneNumber, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("select count(*) from Teacher as u where u.phone_num=\'%s\' and u.pwd_md=\'%s\'",phoneNumber,passwd);
        Query query = session.createQuery(sql);
        int count = ((Long) query.uniqueResult()).intValue();
        session.close();
        return count > 0;

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
        String sql = String.format("from Teacher as u where u.garten_id=%ld",gartenID);
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
