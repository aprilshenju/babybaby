package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.util.MD5;
import com.umeijia.vo.Administrator;
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
@Repository("administratordao")
public class AdministratorDao {
    public AdministratorDao(){

    }
    public Administrator queryAdministrator(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Administrator as u where u.id=%d and u.valid=1", id);
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
        String sql = String.format("from Administrator as u where u.phone_num=\'%s\' and u.valid=1", phoneNumber);
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
        String sql = String.format("from Administrator as u where u.email=\'%s\' and u.valid=1", email);
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


    public boolean verifyToken(long id,String tkn){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("select u.expire from Administrator as u where u.id=%d and u.token=\'%s\' and u.valid=1",id,tkn);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Date dead=(Date)list.get(0);
            Date now=new Date();
            if(dead.after(now)){
                return  true;  //还有效
            }
        }
        return  false;
    }
    /**
     * 登陆成功，设置tkn和有效时间
     * **/
    public Administrator loginCheckByPhone(String phoneNumber, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Administrator as u where u.phone_num=\'%s\' and u.valid=1", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        if(list.size()>0) {
            Administrator administrator = (Administrator) list.get(0);
            if(passwd.equals(administrator.getPwd_md())){ //密码匹配
                try {
                    session.setFlushMode(FlushMode.AUTO);
                    session.beginTransaction();
                    administrator.setToken(MD5.GetSaltMD5Code(administrator.getPhone_num()+passwd+new Date().toString())); //登陆时，即重新计算token，保存数据库
                    Date now=new Date();
                    Date dead = new Date(now .getTime() + DBManager.EXPIRE_SECONDS); //两个小时有效期
                    administrator.setExpire(dead);
                    session.update(administrator);
                    session.flush();
                    session.getTransaction().commit();
                } catch (HibernateException e) {
                    e.printStackTrace();
                    session.getTransaction().rollback();
                    administrator=null;
                } finally{
                    session.close();
                    return administrator;
                }
            }
        }
        return null;
    }

    /**
     * 登陆成功，设置tkn和有效时间
     * **/
    public Administrator loginCheckByEmail(String email, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Administrator as u where u.email=\'%s\' and u.valid=1", email);
        Query query = session.createQuery(sql);
        List list = query.list();
        if(list.size()>0) {
            Administrator administrator = (Administrator) list.get(0);
            if(passwd.equals(administrator.getPwd_md())){ //密码匹配
                try {
                    session.setFlushMode(FlushMode.AUTO);
                    session.beginTransaction();
                    administrator.setToken(MD5.GetSaltMD5Code(administrator.getPhone_num()+passwd+new Date().toString())); //登陆时，即重新计算token，保存数据库
                    Date now=new Date();
                    Date dead = new Date(now .getTime() + DBManager.EXPIRE_SECONDS); //两个小时有效期
                    administrator.setExpire(dead);
                    session.update(administrator);
                    session.flush();
                    session.getTransaction().commit();
                } catch (HibernateException e) {
                    e.printStackTrace();
                    session.getTransaction().rollback();
                    administrator=null;
                } finally{
                    session.close();
                    return administrator;
                }
            }
        }
        return  null;
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
            String hql=String.format("update Administrator admin set admin.pwd_md=\'%s\' where admin.phone_num=\'%s\' and admin.valud=1",passwd,phoneNumber);
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

    /**
     * 将该角色设为无效
     * **/
    public boolean invalidAdministrator(long id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Administrator u set u.valid=%d where u.id=%d",0,id);
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
