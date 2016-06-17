package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.Administrator;
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
@Repository("administratordao")
public class AdministratorDao {
    public AdministratorDao(){

    }
    public Administrator queryAdministrator(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Administrator as u where u.id=%ld", id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Administrator admin = (Administrator) list.get(0);
            return admin;
        }else {
            return null;
        }
    }

    public Administrator queryAdministrator(String phoneNumber) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Administrator as u where u.phone_num=\'%s\'", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Administrator admin = (Administrator) list.get(0);
            return admin;
        }else {
            return null;
        }
    }

    public Administrator queryAdministratorByEmail(String email) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Administrator as u where u.email=\'%s\'", email);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Administrator admin = (Administrator) list.get(0);
            return admin;
        }else {
            return null;
        }
    }

    public boolean loginCheck(String phoneNumber, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("select count(*) from Administrator as u where u.phone_num=\'%s\' and u.pwd_md=\'%s\'",phoneNumber,passwd);
        Query query = session.createQuery(sql);
        int count = ((Long) query.uniqueResult()).intValue();
        session.close();
        return count > 0;

    }

    public boolean addAdministrator(Administrator admin) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(admin);
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

    public boolean correctAdministratorPasswd(String phoneNumber,String passwd) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Administrator admin set admin.pwd_md=\'%s\' where admin.phone_num=\'%s\'",passwd,phoneNumber);
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

    public boolean updateAdministrator(Administrator admin) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(admin);
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

    public boolean deleteAdministrator(String phoneNumber) {
        boolean result=false;
        Administrator admin = queryAdministrator(phoneNumber);
        if(admin!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(admin);
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
