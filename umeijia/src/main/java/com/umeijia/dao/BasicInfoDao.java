package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.BasicInfo;
import com.umeijia.vo.Camera;
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
@Repository("basicinfodao")
public class BasicInfoDao {
    public BasicInfoDao(){

    }

    public BasicInfo queryBasicInfo() {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BasicInfo");
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            BasicInfo info = (BasicInfo) list.get(0);
            return info;
        }else {
            return null;
        }
    }

    public boolean addBasicInfo(BasicInfo info) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(info);
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


    public boolean updateBasicInfo(BasicInfo info) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(info);
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
