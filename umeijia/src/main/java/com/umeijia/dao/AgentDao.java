package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.util.MD5;
import com.umeijia.vo.Agent;
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
@Repository("agentdao")
public class AgentDao {
    public AgentDao(){

    }

    public Agent queryAgent(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.id=%d and u.valid=1", id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Agent agent = (Agent) list.get(0);
            return agent;
        }else {
            return null;
        }
    }

    public Agent queryAgentByName(String name) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.name=%s and u.valid=1", name);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Agent agent = (Agent) list.get(0);
            return agent;
        }else {
            return null;
        }
    }

    public List<Agent> queryAgents() {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.valid=1");
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public Agent queryAgent(String phoneNumber) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.phone_num=\'%s\' and u.valid=1", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Agent agent = (Agent) list.get(0);
            return agent;
        }else {
            return null;
        }
    }

    public Agent queryAgentByEmail(String email) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.email=\'%s\' and u.valid=1", email);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Agent agent = (Agent) list.get(0);
            return agent;
        }else {
            return null;
        }
    }
/*
    public boolean loginCheck(String phoneNumber, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("select count(*) from Agent as u where u.phone_num=\'%s\' and u.pwd_md=\'%s\'",phoneNumber,passwd);
        Query query = session.createQuery(sql);
        int count = ((Long) query.uniqueResult()).intValue();
        session.close();
        return count > 0;

    }*/

    public boolean verifyToken(long id,String tkn){
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("select u.expire from Agent as u where u.id=%d and u.token=\'%s\' and u.valid=1",id,tkn);
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
    public Agent loginCheckByPhone(String phoneNumber, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.phone_num=\'%s\' and u.valid=1", phoneNumber);
        Query query = session.createQuery(sql);
        List list = query.list();
        if(list.size()>0) {
            Agent agent = (Agent) list.get(0);
         //   LockerLogger.log.info("找到了代理商");
            if(passwd.equals(agent.getPwd_md())){ //密码匹配
                try {
               //     LockerLogger.log.info("代理商密码也匹配");
                    session.setFlushMode(FlushMode.AUTO);
                    session.beginTransaction();
                    agent.setToken(MD5.GetSaltMD5Code(agent.getPhone_num()+passwd+new Date().toString())); //登陆时，即重新计算token，保存数据库
                    Date now=new Date();
                    Date dead = new Date(now .getTime() + DBManager.EXPIRE_SECONDS); //两个小时有效期
                    agent.setExpire(dead);
                    session.update(agent);
                //    LockerLogger.log.info("加盟商登陆成功");
                    session.flush();
                    session.getTransaction().commit();
                } catch (HibernateException e) {
               //     LockerLogger.log.info("加盟商登陆异常....");
                    e.printStackTrace();
                    session.getTransaction().rollback();
                   agent=null;
                } finally{
                    session.close();
                    return agent;
                }
            }
        }
        return  null;
    }

    /**
     * 登陆成功，设置tkn和有效时间
     * **/
    public Agent loginCheckByEmail(String email, String passwd) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Agent as u where u.email=\'%s\' and u.valid=1", email);
        Query query = session.createQuery(sql);
        List list = query.list();
        if(list.size()>0) {
            Agent agent = (Agent) list.get(0);
            if(passwd.equals(agent.getPwd_md())){ //密码匹配
                try {
                    session.setFlushMode(FlushMode.AUTO);
                    session.beginTransaction();
                    agent.setToken(MD5.GetSaltMD5Code(agent.getPhone_num()+passwd+new Date().toString())); //登陆时，即重新计算token，保存数据库
                    Date now=new Date();
                    Date dead = new Date(now .getTime() + DBManager.EXPIRE_SECONDS); //两个小时有效期
                    agent.setExpire(dead);
                    session.update(agent);
                    session.flush();
                    session.getTransaction().commit();
                } catch (HibernateException e) {
                    e.printStackTrace();
                    session.getTransaction().rollback();
                    agent=null;
                } finally{
                    session.close();
                    return agent;
                }
            }
        }
        return null;
    }

    
    public boolean addAgent(Agent agent) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(agent);
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

    public boolean correctAgentPasswd(String phoneNumber,String passwd) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Agent agent set agent.pwd_md=\'%s\' where agent.phone_num=\'%s\' and agent.valid=1",passwd,phoneNumber);
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

    public boolean updateAgent(Agent agent) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(agent);
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
     * 将该角色设为无效
     * **/
    public boolean invalidAgent(long id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Agent u set u.valid=%d where u.id=%d",0,id);
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

    public boolean deleteAgent(String phoneNumber) {
        boolean result=false;
        Agent agent = queryAgent(phoneNumber);
        if(agent!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(agent);
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
