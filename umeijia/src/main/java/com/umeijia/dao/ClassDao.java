package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.Class;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by shenju on 2016/6/14.
 * 待优化问题：目前更新属性值，是先查询对象，然后更新整个对象的。并不高效。
 * 应当是直接修改字段，用update操作
 *
 *
 */
@Scope("prototype")
@Repository("classdao")
public class ClassDao {
    public ClassDao(){

    }
    public Class queryClass(long class_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Class as c where c.id=%ld", class_id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Class cla = (Class) list.get(0);
            return cla;
        }else {
            return null;
        }
    }

    public String getTeacherContacts(long class_id){
        String teacherContacts="";
        Session session = DBManager.getSession();
        session.clear();
        String hql = String.format("select teachers_contacts from Class as c where c.id=%ld",class_id);
        Query query = session.createQuery(hql);
        //默认查询出来的list里存放的是一个Object数组，还需要转换成对应的javaBean。
        List<Object> list = query.list();
        if(list.size()>0){
            teacherContacts=(String) list.get(0);
        }
        teacherContacts="";
        return  teacherContacts;
    }

    public boolean setTeacherContacts(long class_id,String teacherContacts){
         boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Class c set c.teachers_contacts=\'%s\' where c.id=%ld",teacherContacts,class_id);
            Query queryupdate=session.createQuery(hql);
            int ret=queryupdate.executeUpdate();
            session.flush();
            session.getTransaction().commit();
            if(ret>=0)
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

    public String getParentsContacts(long class_id){
        String parentsContacts="";
        Session session = DBManager.getSession();
        session.clear();
        String hql = String.format("select parents_contacts from Class as c where c.id=%ld",class_id);
        Query query = session.createQuery(hql);
        //默认查询出来的list里存放的是一个Object数组，还需要转换成对应的javaBean。
        List<Object> list = query.list();
        if(list.size()>0){
            parentsContacts=(String) list.get(0);
        }
        parentsContacts="";
        return  parentsContacts;
    }

    public boolean setParentsContacts(long class_id,String parentsContacts){
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Class c set c.parents_contacts=\'%s\' where c.id=%ld",parentsContacts,class_id);
            Query queryupdate=session.createQuery(hql);
            int ret=queryupdate.executeUpdate();
            session.flush();
            session.getTransaction().commit();
            if(ret>=0)
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

    public long getSchoolID(long class_id){
        long schoolID=-1;
        Session session = DBManager.getSession();
        session.clear();
        String hql = String.format("select school_id from Class as c where c.id=%ld",class_id);
        Query query = session.createQuery(hql);
        //默认查询出来的list里存放的是一个Object数组，还需要转换成对应的javaBean。
        List<Object> list = query.list();
        if(list.size()>0){
            schoolID=(Long) list.get(0);
        }
        schoolID=-1;
        return  schoolID;
    }



}
