package com.umeijia.dao;

import com.umeijia.util.DBManager;
import com.umeijia.vo.BabyShowtime;
import com.umeijia.vo.Pager;
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
@Repository("babyshowtimedao")
public class BabyShowtimeDao {
    public BabyShowtimeDao(){

    }

    public BabyShowtime queryBabyShowtime(long id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyShowtime as bs where bs.id=%d and bs.valid=1",id);
        Query query = session.createQuery(sql);
        List list = query.list();
        session.close();
        if(list.size()>0){
            BabyShowtime showtime = (BabyShowtime) list.get(0);
            return showtime;
        }else {
            return null;
        }
    }

   /* *//**
     * 后续改为分页处理
     * **//*
    public List<BabyShowtime> queryBabyFootprints(long baby_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyShowtime as ba where ba.baby_id=%d  order by ba.date desc",baby_id);
        Query query = session.createQuery(sql);
        List <BabyShowtime> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }*/
    
    /**
     * 后续改为分页处理  
     * **/
    public List<BabyShowtime> queryBabyShowtimesByBaby(long baby_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyShowtime as ba where ba.baby_id=%d and ba.valid=1 order by ba.date desc",baby_id);
        Query query = session.createQuery(sql);
        List <BabyShowtime> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    /**
     * 后续改为分页处理
     * **/
    public List<BabyShowtime> queryBabyShowtimesByParents(long parent_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyShowtime as ba where ba.parent_id=%d and ba.valid=1 order by ba.date desc",parent_id);
        Query query = session.createQuery(sql);
        List <BabyShowtime> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    /**
     * 后续改为分页处理
     * **/
    public List<BabyShowtime> queryBabyShowtimesByTeacher(long teacher_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyShowtime as ba where ba.teacher_id=%d and ba.valid=1 order by ba.date desc",teacher_id);
        Query query = session.createQuery(sql);
        List <BabyShowtime> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    /**
     * 后续改为分页处理
     * **/
    public List<BabyShowtime> queryBabyShowtimesByClass(long class_id) {
        Session session = DBManager.getSession();
        session.clear();
        String sql = String.format("from BabyShowtime as ba where ba.class_id=%d and ba.valid=1 order by ba.date desc",class_id);
        Query query = session.createQuery(sql);
        List <BabyShowtime> list = query.list();
        session.close();
        if(list.size()>0){
            return list;
        }else {
            return null;
        }
    }

    /**
     * 后续改为分页处理,page传入 每页多少项，当前需要第几页的内容。返回总项目数（总页数可通过计算获得）。
     * **/
    public Pager queryBabyShowtimesPageByClass(long class_id,Pager pager) {
        if (pager == null) {
            pager = new Pager();
        }
        Integer pageNumber = pager.getPageNumber();
        Integer pageSize = pager.getPageSize();
        String hql=String.format("from BabyShowtime bs where bs.class_id=%d",class_id);
        String countHql="select count(*) "+hql.substring(hql.indexOf("from"));
        Session session=DBManager.getSession();
        Query query=session.createQuery(countHql);
        int totalRecord=Integer.valueOf(query.uniqueResult()+"");
        query=session.createQuery(hql);

        query.setFirstResult(pageSize*(pageNumber-1));
        query.setMaxResults(pageSize);
        List<BabyShowtime> list=(List<BabyShowtime>)query.list();
        Pager newPage=new Pager();
        newPage.setPageSize(pageSize);
        newPage.setTotalCount(totalRecord);
        newPage.setList(list);
        return newPage;
    }



    public boolean invalidShowtime(long fp_id) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            String hql=String.format("update BabyShowtime bs set bs.valid=0 where bs.id=%d",fp_id);
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

    public boolean addBabyShowtime(BabyShowtime showtime) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.save(showtime);
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



    public boolean updateBabyShowtime(BabyShowtime showtime) {
        boolean result=false;
        Session session = DBManager.getSession();
        try {
            session.setFlushMode(FlushMode.AUTO);
            session.beginTransaction();
            session.update(showtime);
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


    public boolean deleteBabyShowtime(long id) {
        boolean result=false;
        BabyShowtime showtime = queryBabyShowtime(id);
        if(showtime!=null){
            Session session = DBManager.getSession();
            try {
                session.setFlushMode(FlushMode.AUTO);
                session.beginTransaction();
                session.delete(showtime);
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
