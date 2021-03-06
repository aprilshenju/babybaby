package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.Pager;
import com.umeijia.vo.Student;
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
 */
@Scope("prototype")
@Repository("studentdao")
public class StudentDao {
    public StudentDao(){

    }
    public Student queryStudent(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Student as u where u.id=%d and u.valid=1", id);
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

    /**
     * 根据班级返回所有学生的id，用于查看未读家长时使用
     * @param classId
     * @return
     */
    public List<Long> queryStudentByClass(long classId){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Student as u where u.cla.id=%d and u.valid=1", classId);
        Query query = session.createQuery(sql);
        List list = query.list();
        List<Long> result = new ArrayList<Long>();
        if(list!=null){
            for(int i=0;i<list.size();i++){
                result.add(((Student)list.get(i)).getId());
            }
        }
        session.close();
        if(result.size()>0){
           return result;
        }else {
            return null;
        }
    }

    public List<Student> queryStudentByClassId(long classId){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Student as u where u.cla.id=%d and u.valid=1", classId);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List<Student> queryStudentByStudentName(String name){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Student as u where u.name=\'%s\' and u.valid=1", name);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List<Student> queryStudentByClassIdAndStudentName(long classId,String name){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Student as u where u.cla.id=%d and u.name=\'%s\' and u.valid=1", classId,name);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }


    public Pager queryStudentBySchool(long schoolId,Pager pager){

        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql=String.format("from Student as u where u.school_id=%d and u.valid=1", schoolId);
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session=DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
        List<Student> list=(List<Student>)query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
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

    /**
     * 将该角色设为无效
     * **/
    public boolean invalidStudent(long id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Student u set u.valid=0,u.cla.id=0 where u.id=%d",id);
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
