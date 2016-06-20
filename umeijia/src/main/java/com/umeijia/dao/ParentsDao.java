package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.util.MD5;
import com.umeijia.vo.Parents;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by shenju on 2016/6/14.
 */
@Scope("prototype")
@Repository("parentsdao")
public class ParentsDao {
    public ParentsDao(){

    }

    public Parents queryParents(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Parents as u where u.id=%ld", id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Parents parents = (Parents) list.get(0);
            return parents;
        }else {
            return null;
        }
    }

    public Parents queryParents(String phoneNumber) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Parents as u where u.phone_num=\'%s\'", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Parents parents = (Parents) list.get(0);
            return parents;
        }else {
            return null;
        }
    }

    public Parents queryParentsByEmail(String email) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Parents as u where u.email=\'%s\'", email);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Parents parents = (Parents) list.get(0);
            return parents;
        }else {
            return null;
        }
    }
/*
    public boolean loginCheck(String phoneNumber, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("select count(*) from Parents as u where u.phone_num=\'%s\' and u.pwd_md=\'%s\'",phoneNumber,passwd);
        Query query = session.createQuery(sql);
        int count = ((Long) query.uniqueResult()).intValue();
        session.close();
        return count > 0;

    }*/

    public boolean verifyToken(long id,String tkn){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("select u.expire from Parents as u where u.id=%ld and u.token=\'%s\'",id,tkn);
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
    public boolean loginCheckByPhone(String phoneNumber, String passwd) {
        boolean result=false;
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Parents as u where u.phone_num=\'%s\'", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        if(list.size()>0) {
            Parents parents = (Parents) list.get(0);
            if(passwd.equals(parents.getPwd_md())){ //密码匹配
                try {
                    session.setFlushMode(FlushMode.AUTO);
                    session.beginTransaction();
                    parents.setToken(MD5.GetSaltMD5Code(parents.getPhone_num()+passwd+new Date().toString())); //登陆时，即重新计算token，保存数据库
                    Date now=new Date();
                    Date dead = new Date(now .getTime() + 7200000); //两个小时有效期
                    parents.setExpire(dead);
                    session.update(parents);
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
        return  false;
    }

    /**
     * 登陆成功，设置tkn和有效时间
     * **/
    public boolean loginCheckByEmail(String email, String passwd) {
        boolean result=false;
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Parents as u where u.email=\'%s\'", email);
        Query query = session.createQuery(sql);
        List list = query.list();
        if(list.size()>0) {
            Parents parents = (Parents) list.get(0);
            if(passwd.equals(parents.getPwd_md())){ //密码匹配
                try {
                    session.setFlushMode(FlushMode.AUTO);
                    session.beginTransaction();
                    parents.setToken(MD5.GetSaltMD5Code(parents.getPhone_num()+passwd+new Date().toString())); //登陆时，即重新计算token，保存数据库
                    Date now=new Date();
                    Date dead = new Date(now .getTime() + 7200000); //两个小时有效期
                    parents.setExpire(dead);
                    session.update(parents);
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
        return  false;
    }


    public boolean addParents(Parents parents) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(parents);
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

    public boolean correctParentsPasswd(String phoneNumber,String passwd) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Parents parents set parents.pwd_md=\'%s\' where parents.phone_num=\'%s\'",passwd,phoneNumber);
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
    public boolean correctParentsName(String phoneNumber,String name) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Parents parents set parents.name=\'%s\' where parents.phone_num=\'%s\'",name,phoneNumber);
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
    public boolean correctParentsVip(String phoneNumber,boolean is_vip) {
        boolean result=false;
        Session session = DBManager.getSession();
        int vip=0;
        if(is_vip)
            vip=1;

        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Parents parents set parents.is_vip=%d where parents.phone_num=\'%s\'",vip,phoneNumber);
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

    public boolean correctParentsAppPush(String phoneNumber,boolean is_push) {
        boolean result=false;
        Session session = DBManager.getSession();
        int push=0;
        if(is_push)
            push=1;

        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Parents parents set parents.allow_app_push=%d where parents.phone_num=\'%s\'",push,phoneNumber);
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

    public boolean correctParentsWechatPush(String phoneNumber,boolean is_push) {
        boolean result=false;
        Session session = DBManager.getSession();
        int push=0;
        if(is_push)
            push=1;

        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Parents parents set parents.allow_wechat_push=%d where parents.phone_num=\'%s\'",push,phoneNumber);
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

    public boolean updateParents(Parents parents) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(parents);
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
 * 一个幼儿园的所有老师集合
 * **/
    public List<Parents> queryParentssByGarten(long gartenID) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Parents as u where u.garten_id=%ld",gartenID);
        Query query = session.createQuery(sql);
        List list =query.list();
        List<Parents> parentss=new ArrayList<Parents>();
        session.close();
        for (int i = 0; i < list.size(); i++){
            Parents t=(Parents)list.get(i);
            parentss.add(t);
        }
        return parentss;
    }

    public boolean deleteParents(String phoneNumber) {
        boolean result=false;
        Parents parents = queryParents(phoneNumber);
        if(parents!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(parents);
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
