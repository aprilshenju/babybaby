package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.BabyFootPrint;
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
@Repository("babyfootprintdao")
public class BabyFootPrintDao {
    public BabyFootPrintDao(){

    }

    public BabyFootPrint queryBabyFootPrint(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyFootPrint as bs where bs.id=%d and bs.valid=1",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            BabyFootPrint footprint = (BabyFootPrint) list.get(0);
            return footprint;
        }else {
            return null;
        }
    }

   /**
     * 后续改为分页处理
     * **/
    public List<BabyFootPrint> queryBabyFootprints(long baby_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyFootPrint as ba where ba.baby_id=%d and bs.valid=1 order by ba.date desc",baby_id);
        Query query = session.createQuery(sql);
        List <BabyFootPrint> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }
    
    public boolean invalidShowtime(long fp_id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update BabyFootPrint bs set bs.valid=0 where bs.id=%d",fp_id);
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

    public boolean addBabyFootPrint(BabyFootPrint footprint) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(footprint);
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



    public boolean updateBabyFootPrint(BabyFootPrint footprint) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(footprint);
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


    public boolean deleteBabyFootPrint(long id) {
        boolean result=false;
        BabyFootPrint footprint = queryBabyFootPrint(id);
        if(footprint!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(footprint);
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
