package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.Student;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by shenju on 2016/6/14.
 */
@Scope("prototype")
@Repository("studentdao")
public class StudentDao {
    public StudentDao(){

    }
    public Student queryStudent(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Student as u where u.id=%d", id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Student student = (Student) list.get(0);
            return student;
        }else {
            return null;
        }
    }

    public boolean addStudent(Student student) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(student);
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

    public boolean updateStudent(Student student) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(student);
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

    public boolean deleteStudent(long id) {
        boolean result=false;
        Student student = queryStudent(id);
        if(student!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(student);
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
