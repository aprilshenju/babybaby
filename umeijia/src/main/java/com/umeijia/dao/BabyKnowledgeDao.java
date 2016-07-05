package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.BabyKnowledge;
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
@Repository("babyknowledgedao")
public class BabyKnowledgeDao {
    public BabyKnowledgeDao(){

    }

    public BabyKnowledge queryBabyKnowledge(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyKnowledge as knowledge where knowledge.id=%d and valid=1",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            BabyKnowledge knowledge = (BabyKnowledge) list.get(0);
            return knowledge;
        }else {
            return null;
        }
    }

    public List<BabyKnowledge> queryBabyKnowledgeByTitle(String title) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyKnowledge as knowledge where knowledge.question=%d and valid=1",title);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    public List getBabyKnowledgeList() {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyKnowledge where valid=1");
        Query query = session.createQuery(sql);
        List <BabyKnowledge> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }
    
    public boolean addBabyKnowledge(BabyKnowledge knowledge) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(knowledge);
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

    public boolean updateBabyKnowledge(BabyKnowledge knowledge) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(knowledge);
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

    public boolean invaliBabyKnowledge(long g_id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update BabyKnowledge bs set bs.valid=0 where bs.id=%d",g_id);
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


    public boolean deleteBabyKnowledge(long id) {
        boolean result=false;
        BabyKnowledge bk = queryBabyKnowledge(id);
        if(bk!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(bk);
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
