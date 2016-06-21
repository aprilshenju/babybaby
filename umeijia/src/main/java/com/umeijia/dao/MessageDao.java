package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.Message;
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
@Repository("messagedao")
public class MessageDao {
    public MessageDao(){

    }

    public Message queryMessage(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Message as message where message.id=%d",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            Message message = (Message) list.get(0);
            return message;
        }else {
            return null;
        }
    }
    
    /**
     * teacher_id 和 parents_id共同标识一个会话
     * **/
    public List<Message> queryUnreadMessages(long teacher_id,long parents_id,int dir) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Message as message where message.teacher_id=%d and message.parents_id=%d and message.send_direction=%d and message.read_or_not=0 order by message.date desc",teacher_id,parents_id,dir);
        Query query = session.createQuery(sql);
        List <Message> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List<Message> queryRecent5Messages(long teacher_id,long parents_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from Message as message where message.teacher_id=%d and message.parents_id=%d order by message.date desc",teacher_id,parents_id);
        Query query = session.createQuery(sql);
        List <Message> list = query.list();
        session.close();
        if(list.size()>0){
            if(list.size()>5)
                return  list.subList(0,4);
            else
                return list;
        }else {
            return null;
        }
    }

    /***
     * 阅读某条消息
     * **/
    public boolean readMessage(long id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update Message mg set mg.read_or_not=1 where mg.id=%d",id);
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

    public boolean addMessage(Message message) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(message);
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


    public boolean updateMessage(Message message) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(message);
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


    public boolean deleteMessage(long id) {
        boolean result=false;
        Message message = queryMessage(id);
        if(message!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(message);
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
