package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.FeedBack;
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
@Repository("feedbackdao")
public class FeedBackDao {
    public FeedBackDao(){

    }

    public FeedBack queryFeedBack(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from FeedBack as fb where fb.id=%d",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            FeedBack fb = (FeedBack) list.get(0);
            return fb;
        }else {
            return null;
        }
    }

    public List getFeedBackList() {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from FeedBack");
        Query query = session.createQuery(sql);
        List <FeedBack> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }
    
    public boolean addFeedBack(FeedBack feed_back) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(feed_back);
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



    public boolean updateFeedBack(FeedBack feed_back) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(feed_back);
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


    public boolean deleteFeedBack(long id) {
        boolean result=false;
        FeedBack fb = queryFeedBack(id);
        if(fb!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(fb);
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
