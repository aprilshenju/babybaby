package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.util.MD5;
import com.umeijia.vo.Agent;
import com.umeijia.vo.SMSMessage;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by shenju on 2016/6/14.
 */
@Scope("prototype")
@Repository("smsmessagedao")
public class SMSMessageDao {
    public SMSMessageDao(){

    }

    public SMSMessage queryMessage(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from SMSMessage as u where u.id=%d", id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            SMSMessage sMSMessage = (SMSMessage) list.get(0);
            return sMSMessage;
        }else {
            return null;
        }
    }

    public SMSMessage querySMSMessageByPhone(String phoneNumber) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from SMSMessage as u where u.phoneNum=\'%s\'", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            SMSMessage sMSMessage = (SMSMessage) list.get(0);
            return sMSMessage;
        }else {
            return null;
        }
    }



    
    public boolean addSMSMessage(SMSMessage sMSMessage) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(sMSMessage);
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



    public boolean updateAgent(SMSMessage sMSMessage) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(sMSMessage);
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

    public boolean deleteSMSMessage(String phoneNumber) {
        boolean result=false;
        SMSMessage sMSMessage = querySMSMessageByPhone(phoneNumber);
        if(sMSMessage!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(sMSMessage);
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
