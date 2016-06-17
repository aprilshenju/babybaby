package com.umeijia.dao;
import com.umeijia.util.DBManager;
import com.umeijia.vo.GartenNews;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by shenju on 2016/6/17.
 */
@Scope("prototype")
@Repository("gartennewsdao")
public class GartenNewsDao {
    public GartenNewsDao(){
        
    }


    public GartenNews queryGartenNews(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from GartenNews as gnews where gnews.id=%ld",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            GartenNews gnews = (GartenNews) list.get(0);
            return gnews;
        }else {
            return null;
        }
    }

    /**
     *    // 按 agent获取
     * **/
    public List<GartenNews> queryGartenNewss(long school_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from GartenNews as gnews where gnews.school_id=%ld order by gnews.date desc",school_id);
        Query query = session.createQuery(sql);
        List <GartenNews> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public boolean addGartenNews(GartenNews gnews) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(gnews);
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


    public boolean updateGartenNews(GartenNews gnews) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(gnews);
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


    public boolean deleteGartenNews(long id) {
        boolean result=false;
        GartenNews gnews = queryGartenNews(id);
        if(gnews!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(gnews);
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

