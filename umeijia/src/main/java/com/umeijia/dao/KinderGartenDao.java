package com.umeijia.dao;
import com.umeijia.util.DBManager;
import com.umeijia.vo.Kindergarten;
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
@Repository("kindergartendao")
public class KinderGartenDao {
    public KinderGartenDao(){
        
    }


    public Kindergarten queryKindergarten(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Kindergarten as garten where garten.id=%d",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Kindergarten garten = (Kindergarten) list.get(0);
            return garten;
        }else {
            return null;
        }
    }


    public List<Kindergarten> queryKindergartenBySchoolName(String schoolName) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Kindergarten as garten where garten.name like \'%%%s%%\'",schoolName);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    /**
     *    // 按 agent获取
     * **/
    public List<Kindergarten> queryKindergartens(long agent_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Kindergarten as garten where garten.agent.id=%d order by garten.create_date desc",agent_id);
        Query query = session.createQuery(sql);
        List <Kindergarten> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List<Kindergarten> queryKindergartens() {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Kindergarten");
        Query query = session.createQuery(sql);
        List <Kindergarten> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public boolean addKindergarten(Kindergarten garten) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(garten);
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


    public boolean updateKindergarten(Kindergarten garten) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(garten);
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
     * 将该班级设为无效
     * **/
    public boolean invalidGarten(long id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Kindergarten u set u.valid=0,u.agent=0 where u.id=%d",id);
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

    public boolean deleteKindergarten(long id) {
        boolean result=false;
        Kindergarten garten = queryKindergarten(id);
        if(garten!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(garten);
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

