package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.ClassActivity;
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
@Repository("classactivitydao")
public class ClassActivityDao {
    public ClassActivityDao(){

    }

    public ClassActivity queryClassActivity(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassActivity as ca where ca.id=%d and valid=1",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            ClassActivity activity = (ClassActivity) list.get(0);
            return activity;
        }else {
            return null;
        }
    }

    /**
     * 后续改为分页处理,page传入 每页多少项，当前需要第几页的内容。返回总项目数（总页数可通过计算获得）。
     * **/
    public Pager queryClassActivityPageByClass(long class_id, Pager pager) {
        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql=String.format("from ClassActivity bs where bs.class_id=%d and valid=1",class_id);
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session=DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
        List<ClassActivity> list=(List<ClassActivity>)query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
    }


    public List queryOneClassActivitysList(long class_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassActivity as ca where ca.class_id=%d and valid=1 order by ca.start_date desc",class_id);
        Query query = session.createQuery(sql);
        List <ClassActivity> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List queryOneClassActivitysListByClassAndTitle(long class_id,String title) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassActivity as ca where ca.class_id=%d and ca.title=\'%s\' and valid=1 order by ca.start_date desc",class_id,title);
        Query query = session.createQuery(sql);
        List <ClassActivity> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List queryOneClassActivitysListByTitle(String title) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassActivity as ca where  ca.title=\'%s\' and valid=1 order by ca.start_date desc",title);
        Query query = session.createQuery(sql);
        List <ClassActivity> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List queryOneClassActivitysListBySchoolId(long schoolId) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ClassActivity as ca where ca.school_id=%d and valid=1 order by ca.start_date desc",schoolId);
        Query query = session.createQuery(sql);
        List <ClassActivity> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public boolean addClassActivity(ClassActivity activity) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(activity);
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

    public boolean updateClassActivity(ClassActivity activity) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(activity);
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

    public boolean invalidClassActivity(long g_id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update ClassActivity bs set bs.valid=0 where bs.id=%d",g_id);
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



    public boolean deleteClassActivity(long id) {
        boolean result=false;
        ClassActivity activity = queryClassActivity(id);
        if(activity!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(activity);
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
