package com.umeijia.dao;
import com.umeijia.util.DBManager;
import com.umeijia.vo.ShowtimeComments;
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
@Repository("showtimecommentsdao")
public class ShowtimeCommentsDao {
    public ShowtimeCommentsDao(){
        
    }


    public ShowtimeComments queryOneShowtimeComments(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ShowtimeComments as comment where comment.id=%ld",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            ShowtimeComments comment = (ShowtimeComments) list.get(0);
            return comment;
        }else {
            return null;
        }
    }

    /**
     *    一条动态的所有评论
     *    评论暂时不 提供删除
     * **/
    public List<ShowtimeComments> queryShowtimeComments(long showtime_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from ShowtimeComments as comment where comment.showtime_id=%ld order by comment.date desc",showtime_id);
        Query query = session.createQuery(sql);
        List <ShowtimeComments> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public boolean addShowtimeComments(ShowtimeComments comment) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(comment);
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


    public boolean updateShowtimeComments(ShowtimeComments comment) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(comment);
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


    public boolean deleteShowtimeComments(long id) {
        boolean result=false;
        ShowtimeComments comment = queryOneShowtimeComments(id);
        if(comment!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(comment);
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

