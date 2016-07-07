package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.CheckinRecords;
import com.umeijia.vo.ClassNotification;
import com.umeijia.vo.Pager;
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
@Repository("classnotificationdao")
public class ClassNotificationDao {
    public ClassNotificationDao(){

    }

    public ClassNotification queryClassNotification(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassNotification as ca where ca.id=%d and valid=1",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            ClassNotification notification = (ClassNotification) list.get(0);
            return notification;
        }else {
            return null;
        }
    }
    
    /**
     * 后续改为分页处理  
     * **/
    public List<ClassNotification> queryClassNotifications(long class_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassNotification as ca where ca.class_id=%d and valid=1 order by ca.date desc",class_id);
        Query query = session.createQuery(sql);
        List <ClassNotification> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List<ClassNotification> queryClassNotificationsByClassAndTitle(long class_id,String title) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassNotification as ca where ca.class_id=%d and ca.title=\'%s\' and valid=1 order by ca.date desc",class_id,title);
        Query query = session.createQuery(sql);
        List <ClassNotification> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List<ClassNotification> queryClassNotificationsByTitle(String title) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassNotification as ca where ca.title=\'%s\' and valid=1 order by ca.date desc",title);
        Query query = session.createQuery(sql);
        List <ClassNotification> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public Pager queryClassNotificationsBySchool(long schoolId,Pager pager) {
        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql=  String.format("from ClassNotification as ca where ca.school_id=%d and valid=1 order by ca.date desc",schoolId);
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session=DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
        List<ClassNotification> list=(List<ClassNotification>)query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
    }

    /**
     * 后续改为分页处理,page传入 每页多少项，当前需要第几页的内容。返回总项目数（总页数可通过计算获得）。
     * **/
    public Pager queryClassNotificationPageByClass(long class_id, Pager pager) {
        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql=String.format("from ClassNotification bs where bs.class_id=%d and valid=1",class_id);
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session=DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
        List<ClassNotification> list=(List<ClassNotification>)query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
    }
    
    public boolean addClassNotification(ClassNotification notification) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(notification);
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

    public  String getNotificationSubscribers(long id){
        String notificationSubscribers="";
        Session session = DBManager.getSession();
        session.clear();
        String hql = String.format("select subscribers from ClassNotification as c where c.id=%d and valid=1",id);
        Query query = session.createQuery(hql);
        //默认查询出来的list里存放的是一个Object数组，还需要转换成对应的javaBean。
        List<Object> list = query.list();
        if(list.size()>0){
            notificationSubscribers=(String) list.get(0);
        }
        notificationSubscribers="";
        return  notificationSubscribers;

    }

    public boolean updateClassNotification(ClassNotification notification) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(notification);
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


    public boolean invalidClassNotification(long g_id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update ClassNotification bs set bs.valid=0 where bs.id=%d",g_id);
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



    public boolean deleteClassNotification(long id) {
        boolean result=false;
        ClassNotification notification = queryClassNotification(id);
        if(notification!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(notification);
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
