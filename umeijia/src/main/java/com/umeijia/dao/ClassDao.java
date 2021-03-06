package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.Class;
import com.umeijia.vo.Kindergarten;
import com.umeijia.vo.Pager;
import com.umeijia.vo.Teacher;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by shenju on 2016/6/14.
 * 待优化问题：目前更新属性值，是先查询对象，然后更新整个对象的。并不高效� * 应当是直接修改字段，用update操作
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
        String sql = String.format("from Class as c where c.id=%d and c.valid=1", class_id);
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

    public List<Class> queryClassBySchoolId(long school_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Class as c where c.garten.id=%d", school_id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){

            return list;
        }else {
            return null;
        }
    }

    public Pager queryClassBySchoolId(long schoolId,Pager pager) {
        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql=String.format("from Class as c where c.garten.id=%d", schoolId);
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session=DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
        List<Class> list=(List<Class>)query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
    }


    public Class queryClassBySchoolIdAndClassName(long schoolId,String className) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Class as c where c.garten.id=%d and c.name=\'%s\'", schoolId,className);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            return (Class)list.get(0);
        }else {
            return null;
        }
    }

    public List<Class> queryClassesByGarten(long garten_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Class as c where c.garten.id=%d and c.valid=1", garten_id);
        Query query = session.createQuery(sql);
        List <Class> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

 /*   public String getTeacherContacts(long class_id){
        String teacherContacts="";
        Session session = DBManager.getSession();
        session.clear();
        String hql = String.format("select c.teachers_contacts from Class as c where c.id=%d",class_id);
        Query query = session.createQuery(hql);
        //默认查询出来的list里存放的是一个Object数组，还需要转换成对应的javaBean�        List<Object> list = query.list();
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
            String hql=String.format("update Class c set c.teachers_contacts=\'%s\' where c.id=%d",teacherContacts,class_id);
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
    }*/

    public String getParentsContacts(long class_id){
        String parentsContacts="";
        Session session = DBManager.getSession();
        session.clear();
        String hql = String.format("select c.parents_contacts from Class as c where c.id=%d and c.valid=1",class_id);
        Query query = session.createQuery(hql);
        //默认查询出来的list里存放的是一个Object数组，还需要转换成对应的javaBean�
        List<Object> list = query.list();
        if(list.size()>0){
            parentsContacts=(String) list.get(0);
            return parentsContacts;
        }
        return  parentsContacts;
    }

    public boolean setParentsContacts(long class_id,String parentsContacts){
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Class c set c.parents_contacts=\'%s\' where c.id=%d and c.valid=1",parentsContacts,class_id);
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

    public Kindergarten getGarten(long class_id){
       Kindergarten garten=null;
        Session session = DBManager.getSession();
        session.clear();
        String hql = String.format("select c.garten from Class as c where c.id=%d and c.valid=1",class_id);
        Query query = session.createQuery(hql);
        //默认查询出来的list里存放的是一个Object数组，还需要转换成对应的javaBean�
        List<Object> list = query.list();
        if(list.size()>0){
            garten=(Kindergarten) list.get(0);
        }
        return  garten;
    }

    public boolean updateClass(Class cla) {
        boolean result = false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(cla);
            session.flush();
            session.getTransaction().commit();
            result = true;
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            result = false;
        } finally {
            session.close();
            return result;
        }
    }

    /**
     * 将该班级设为无效
     * **/
    public boolean invalidClass(long id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Class u set u.valid=0,u.garten.id=0 where u.id=%d",id);
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

    public boolean addClass(Class cla) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(cla);
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


}
