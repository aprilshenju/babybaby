package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.HomeWork;
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
@Repository("homeworkdao")
public class HomeWorkDao {
    public HomeWorkDao(){

    }

    public HomeWork queryHomeWork(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from HomeWork as work where work.id=%ld",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            HomeWork work = (HomeWork) list.get(0);
            return work;
        }else {
            return null;
        }
    }
    
    /**
     * 后续改为分页处理  
     * **/
    public List<HomeWork> queryHomeWorks(long class_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from HomeWork as work where work.class_id=%ld order by work.date desc",class_id);
        Query query = session.createQuery(sql);
        List <HomeWork> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }
    
    public boolean addHomeWork(HomeWork work) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(work);
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


    public boolean updateHomeWork(HomeWork work) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(work);
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


    public boolean deleteHomeWork(long id) {
        boolean result=false;
        HomeWork homework = queryHomeWork(id);
        if(homework!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(homework);
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
